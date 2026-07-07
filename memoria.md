# Memoria Técnica — Práctica Final
## Arquitectura Cloud de Microservicios con Kubernetes e Istio

**Alumno:** Sergio Plaza  
**Fecha de entrega:** 23 de febrero de 2026  
**Módulo:** Cloud Computing — Máster Universitario

---

## 1. Resumen Ejecutivo

Este proyecto implementa una arquitectura de microservicios de tres niveles desplegada en un clúster Kubernetes con gestión de tráfico mediante Istio Service Mesh. La aplicación modela un sistema de comercio electrónico simplificado compuesto por: un frontend web (cliente Thymeleaf), tres microservicios de negocio (Usuarios, Productos y Pedidos) y una base de datos relacional MySQL.

Todas las operaciones requeridas por el enunciado están automatizadas mediante scripts de shell: despliegue de versión 1, despliegue parcial de versión 2, enrutamiento a nodos específicos, pruebas A/B con cabecera HTTP, despliegue completo de versión 2 y rollback. Istio gestiona el tráfico entre servicios mediante `VirtualService` y `DestinationRule`, sin necesidad de modificar el código de aplicación para cambiar el comportamiento de enrutamiento.

---

## 2. Arquitectura del Sistema

### 2.1 Niveles de la Arquitectura

La arquitectura se organiza en tres capas claramente diferenciadas:

```
┌─────────────────────────────────────────────────────────────────┐
│                    NIVEL 1 — FRONT-END                          │
│              client-service (Thymeleaf / Spring Boot)           │
│              v1: client-service:1.0  |  v2: client-service:2.0  │
│                       3 réplicas mínimo                         │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTP REST (via Istio sidecar)
     ┌─────────────────────┼────────────────────┐
     ▼                     ▼                    ▼
┌──────────┐         ┌──────────┐         ┌──────────┐
│  Users   │         │ Products │         │  Orders  │
│    MS    │         │    MS    │         │    MS    │
│  v1 / v2 │         │  v1 / v2 │         │  v1 / v2 │
│ 3 répl.  │         │ 3 répl.  │         │ 3 répl.  │
└────┬─────┘         └─────┬────┘         └──────────┘
     │                     │                    │
     └─────────────────────┴────────────────────┘
                           │ JDBC / MySQL
              ┌────────────▼────────────┐
              │   NIVEL 3 — STORAGE     │
              │       MySQL 9.5.0       │
              │    Base: Cloud_DB       │
              └─────────────────────────┘
```

**NIVEL 1 — Front-end:** Aplicación web Spring Boot con vistas Thymeleaf y Bootstrap 5. Actúa como cliente HTTP de los tres microservicios usando Spring 6 HTTP Interfaces (clientes declarativos). Expuesta externamente mediante Istio Ingress Gateway en el puerto 80.

**NIVEL 2 — Microservicios:** Tres servicios REST independientes desarrollados con Spring Boot 4 y Java 21:
- **users-microservice**: CRUD de usuarios (`/users`).
- **products-microservice**: CRUD de productos (`/products`).
- **orders-microservice**: CRUD de pedidos (`/orders`). Orquesta llamadas a los otros dos servicios para obtener los detalles de usuario y producto.

**NIVEL 3 — Almacenamiento:** Base de datos MySQL 9.5.0 con tres tablas (`Users`, `Products`, `Orders`) y usuarios de base de datos dedicados por microservicio, con permisos mínimos.

### 2.2 Componentes Kubernetes

| Recurso | Tipo | Descripción |
|---|---|---|
| `cloud` | Namespace | Espacio de nombres con inyección automática de sidecar Istio |
| `cloud-db-service` | Service (ClusterIP) | Acceso interno a MySQL |
| `users-microservice-service` | Service (ClusterIP) | Balanceador de carga para pods de Users |
| `products-microservice-service` | Service (ClusterIP) | Balanceador de carga para pods de Products |
| `orders-microservice-service` | Service (ClusterIP) | Balanceador de carga para pods de Orders |
| `client-service` | Service (NodePort 30007) | Exposición del cliente web |
| `cloud-db-pvc` | PersistentVolumeClaim (5Gi) | Almacenamiento persistente MySQL |
| `cloud-gateway` | Gateway (Istio) | Entrada de tráfico externo en puerto 80 |

### 2.3 Componentes Istio

| Recurso | Tipo | Función |
|---|---|---|
| `cloud-gateway` | Gateway | Punto de entrada Istio Ingress para tráfico externo |
| `users-destination-rule` | DestinationRule | Define subsets v1/v2 para users |
| `products-destination-rule` | DestinationRule | Define subsets v1/v2 para products |
| `orders-destination-rule` | DestinationRule | Define subsets v1/v2 para orders |
| `client-destination-rule` | DestinationRule | Define subsets v1/v2 para el cliente |
| `*-virtualservice` (v1/) | VirtualService | Enruta 100% del tráfico a versión 1 |
| `*-virtualservice` (v2/) | VirtualService | Enruta 100% del tráfico a versión 2 |
| `*-virtualservice` (A-B/) | VirtualService | Enruta según cabecera `X-type` |

---

## 3. Implementación de Servicios

### 3.1 Tecnologías Utilizadas

- **Java 21** con **Spring Boot 4.0.2**
- **Spring Data JPA / Hibernate** para acceso a base de datos
- **Spring Web MVC** para APIs REST
- **Spring Actuator** para health checks (`/actuator/health/liveness`, `/actuator/health/readiness`)
- **Thymeleaf** + **Bootstrap 5** para el frontend
- **Spring 6 HTTP Interfaces** para comunicación entre microservicios (cliente declarativo)
- **Maven** con build multi-stage en Docker
- **MySQL Connector/J** para conexión JDBC
- **Docker** con imágenes base `eclipse-temurin:21-jre`

### 3.2 Diferenciación entre Versiones (v1 / v2)

Cada microservicio incluye un campo `version` en todas sus respuestas JSON. El valor de este campo se inyecta mediante la variable de entorno `APP_VERSION` definida en el manifiesto de despliegue:

```yaml
env:
  - name: APP_VERSION
    value: "v1"   # o "v2" en el deployment correspondiente
```

Internamente, la propiedad se lee mediante:

```yaml
# application-docker.yaml
app:
  version: ${APP_VERSION:v1}
```

Y se incluye en la respuesta a través del mapper:

```java
@Value("${app.version:v1}")
private String appVersion;

public UserResponse toUserResponse(User user) {
    UserResponse response = new UserResponse(user.getId(),
        user.getName().toUpperCase(), user.getSurname().toUpperCase());
    response.setVersion(appVersion);
    return response;
}
```

**Ejemplo de respuesta v1:**
```json
{ "id": 1, "name": "SERGIO", "surname": "PLAZA", "version": "v1" }
```

**Ejemplo de respuesta v2:**
```json
{ "id": 1, "name": "SERGIO", "surname": "PLAZA", "version": "v2" }
```

### 3.3 Comunicación Inter-Microservicio con Header X-type

El microservicio de pedidos (Orders) propaga el header HTTP `X-type` al llamar a Users y Products. Esto permite que Istio enrute cada llamada interna a la versión correcta según el contexto de la solicitud:

```java
@HttpExchange("/users")
public interface IUsersClient {
    @GetExchange("/id/{id}")
    UserRequest getUserById(@RequestHeader("X-type") String version, @PathVariable Integer id);
}
```

La versión se selecciona aleatoriamente en tiempo de ejecución para simular tráfico mixto:

```java
private String generateTrafficType() {
    return ThreadLocalRandom.current().nextBoolean() ? "v1" : "v2";
}
```

### 3.4 Gestión de Sesiones

La aplicación cliente gestiona sesiones HTTP de forma nativa a través del servidor de aplicaciones embebido (Tomcat). Cada conexión de usuario mantiene un contexto de sesión independiente. No se implementa autenticación explícita, tal como permite el enunciado.

---

## 4. Base de Datos

### 4.1 Esquema

**Base de datos:** `Cloud_DB` (UTF-8)

```sql
CREATE TABLE Users (
  idUsers INT PRIMARY KEY AUTO_INCREMENT,
  name    VARCHAR(45) NOT NULL,
  surname VARCHAR(60) NOT NULL
);

CREATE TABLE Products (
  idProducts INT PRIMARY KEY AUTO_INCREMENT,
  name       VARCHAR(45) NOT NULL,
  price      FLOAT NOT NULL
);

CREATE TABLE Orders (
  id        INT PRIMARY KEY AUTO_INCREMENT,
  userId    INT NOT NULL,
  productId INT NOT NULL,
  units     INT NOT NULL,
  date      DATETIME NOT NULL
);
```

### 4.2 Seguridad de Base de Datos

Cada microservicio utiliza un usuario MySQL dedicado con permisos mínimos:

| Usuario | Contraseña | Permisos |
|---|---|---|
| `users_manager` | `users_password` | ALL ON Cloud_DB.Users |
| `products_manager` | `products_password` | ALL ON Cloud_DB.Products |
| `orders_manager` | `orders_password` | ALL ON Cloud_DB.Orders + SELECT en Users y Products |

Los credenciales se inyectan mediante Kubernetes Secrets.

---

## 5. Operaciones Realizadas

### Operación 1: Creación y despliegue completo de la aplicación (v1)

**Script:** `kubernetes/scripts/01-deploy-v1.sh`

```bash
#!/bin/bash
NAMESPACE=cloud
# Crear namespace con Istio injection
kubectl create namespace cloud
kubectl label namespace cloud istio-injection=enabled

# Instalar Istio con perfil demo
istioctl install --set profile=demo -y

# Aplicar Gateway de Istio
kubectl apply -n $NAMESPACE -f ../istio/gateway.yaml

# Aplicar DestinationRules y VirtualServices v1
for i in $(ls ../istio/destination-rules/) ; do kubectl apply -n $NAMESPACE -f ../istio/destination-rules/$i ; done
for i in $(ls ../istio/virtual-service/v1/) ; do kubectl apply -n $NAMESPACE -f ../istio/virtual-service/v1/$i ; done

# Desplegar base de datos
kubectl apply -n $NAMESPACE -f ../database/mysql-*.yaml

# Desplegar microservicios v1
kubectl apply -n $NAMESPACE -f ../users-microservice/users-db-secrets.yaml \
  -f ../users-microservice/users-service.yaml \
  -f ../users-microservice/v1/users-deployment.yaml
# (ídem products y orders)

# Desplegar cliente v1
kubectl apply -n $NAMESPACE -f ../client/client-config.yaml \
  -f ../client/client-service.yaml \
  -f ../client/client-deployment.yaml

# Desplegar HPAs
for i in $(ls ../hpa/) ; do kubectl apply -n $NAMESPACE -f ../hpa/$i ; done
```

Resultado: 3 réplicas de cada microservicio arrancando en versión 1, cliente web accesible.

### Operación 2: Despliegue de versión 2 en 2 servicios (Users y Products)

**Script:** `kubernetes/scripts/02-deploy-v2-2S.sh`

Despliega las imágenes `users-service:2.0` y `products-service:2.0` pero mantiene `orders-service:1.0`. Los VirtualServices de users y products se actualizan para enrutar a v2. Orders continúa en v1.

### Operación 3: Enrutamiento a nodos específicos

**Scripts:** `03-route-U1-P2.sh` y `04-route-U2-P1.sh`

Permiten dirigir el tráfico de Users a v1 y Products a v2 (o viceversa) modificando únicamente los VirtualServices sin redeplegar pods:

```bash
# 03-route-U1-P2.sh: Users→v1, Products→v2
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/v1/users-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/v2/products-virtualservice.yaml
```

### Operación 4: Pruebas A/B con cabecera HTTP X-type

**Script:** `kubernetes/scripts/05-route-A-B.sh`

```bash
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/A-B/users-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/A-B/products-virtualservice.yaml
```

Los VirtualServices A/B tienen la siguiente lógica:

```yaml
http:
  - match:
      - headers:
          X-type:
            exact: v2
    route:
      - destination:
          host: users-microservice-service
          subset: v2
  - route:
      - destination:
          host: users-microservice-service
          subset: v1
```

Verificación con curl:
```bash
# → versión 2
curl -H "X-type: v2" http://<NODE_IP>:<GATEWAY_PORT>/users

# → versión 1 (sin cabecera)
curl http://<NODE_IP>:<GATEWAY_PORT>/users
```

### Operación 5: Despliegue de versión 2 para todos los servicios

**Script:** `kubernetes/scripts/06-deploy-v2.sh`

Actualiza los VirtualServices de todos los microservicios y el cliente a v2. Despliega las imágenes v2 de Users, Products y Orders.

### Operación 6: Rollback a versión 1

**Script:** `kubernetes/scripts/07-rollback.sh`

Restaura todos los VirtualServices a v1 y aplica de nuevo los deployments v1 (que Kubernetes detecta como ya existentes y no modifica si no hay cambios, garantizando idempotencia):

```bash
for i in $(ls ../istio/virtual-service/v1/) ; do kubectl apply -n $NAMESPACE -f ../istio/virtual-service/v1/$i ; done
kubectl apply -n $NAMESPACE -f ../users-microservice/v1/users-deployment.yaml
kubectl apply -n $NAMESPACE -f ../products-microservice/v1/products-deployment.yaml
kubectl apply -n $NAMESPACE -f ../orders-microservice/v1/orders-deployment.yaml
```

El rollback es instantáneo a nivel de enrutamiento (Istio actualiza las reglas en <1 segundo) y gradual a nivel de pods (Kubernetes usa rolling update para sustituir pods v2 por v1).

---

## 6. Escalado Automático (HPA)

Cada servicio cuenta con un `HorizontalPodAutoscaler` que mantiene un mínimo de 3 réplicas y puede escalar hasta 10 (8 para el cliente):

```yaml
spec:
  minReplicas: 3
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

El escalado se activa cuando el uso de CPU supera el 70% o el de memoria el 80%, cumpliendo el requisito de escalado dinámico del enunciado.

---

## 7. Automatización del Tráfico para Testing

Para validar automáticamente el enrutamiento sin intervención manual se puede emplear el siguiente conjunto de comandos:

```bash
#!/bin/bash
# Script de verificación de versiones
GATEWAY_URL="http://$(kubectl -n istio-system get service istio-ingressgateway \
  -o jsonpath='{.status.loadBalancer.ingress[0].ip}')"

echo "=== TEST: Tráfico sin cabecera (debe responder v1) ==="
curl -s "$GATEWAY_URL/users/id/1" | python3 -m json.tool

echo "=== TEST: Tráfico con X-type: v2 (debe responder v2) ==="
curl -s -H "X-type: v2" "$GATEWAY_URL/users/id/1" | python3 -m json.tool

echo "=== TEST: 10 llamadas en A/B — distribución de versiones ==="
for i in $(seq 1 10); do
  curl -s "$GATEWAY_URL/users/id/1" | grep -o '"version":"[^"]*"'
done
```

Para un test de carga y verificación de autoscaling:
```bash
# Generar carga con kubectl run (contenedor temporal)
kubectl run -n cloud load-test --image=busybox --rm -it --restart=Never -- \
  sh -c "while true; do wget -q -O- http://users-microservice-service:8000/users; done"

# Observar el HPA en tiempo real
kubectl get hpa -n cloud -w
```

---

## 8. Estructura de Ficheros del Proyecto

```
cloud-microservices/
├── users/                          # Microservicio Usuarios (Spring Boot)
├── products/                       # Microservicio Productos (Spring Boot)
├── orders/                         # Microservicio Pedidos (Spring Boot)
├── client/                         # Frontend Web (Spring Boot + Thymeleaf)
├── db/
│   └── scripts/
│       ├── 01_set_up.sql           # Esquema y usuarios DB
│       └── 02_db_ini.sql           # Datos de prueba
├── docker-compose.yaml             # Entorno local de desarrollo
└── kubernetes/
    ├── database/                   # MySQL: Secret, PVC, Service, Deployment
    ├── users-microservice/
    │   ├── v1/users-deployment.yaml
    │   └── v2/users-deployment.yaml
    ├── products-microservice/
    │   ├── v1/products-deployment.yaml
    │   └── v2/products-deployment.yaml
    ├── orders-microservice/
    │   ├── v1/orders-deployment.yaml
    │   └── v2/orders-deployment.yaml
    ├── client/
    │   ├── client-deployment.yaml  # v1 (réplicas: 3)
    │   └── v2/client-deployment.yaml
    ├── hpa/                        # HorizontalPodAutoscalers
    ├── istio/
    │   ├── gateway.yaml            # Istio Gateway (puerto 80)
    │   ├── destination-rules/      # DestinationRule por servicio
    │   └── virtual-service/
    │       ├── v1/                 # VirtualServices → 100% v1
    │       ├── v2/                 # VirtualServices → 100% v2
    │       └── A-B/                # VirtualServices → X-type header
    └── scripts/
        ├── 01-deploy-v1.sh
        ├── 02-deploy-v2-2S.sh
        ├── 03-route-U1-P2.sh
        ├── 04-route-U2-P1.sh
        ├── 05-route-A-B.sh
        ├── 06-deploy-v2.sh
        └── 07-rollback.sh
```

---

## 9. Debilidades del Trabajo

### 9.1 Base de Datos sin Alta Disponibilidad

MySQL se despliega como instancia única (`replicas: 1`) sin configuración de réplica primaria/secundaria ni clustering. El enunciado indica explícitamente que la capa de almacenamiento no está versionada, pero tampoco está replicada. En un entorno de producción real se utilizaría MySQL Operator con InnoDB Cluster o se sustituiría por una base de datos gestionada (Cloud SQL, RDS). Un fallo del pod de MySQL detiene completamente la aplicación.

### 9.2 Credenciales con Seguridad Limitada

Los secretos de Kubernetes contienen contraseñas en texto plano codificadas en Base64, lo que no es cifrado sino encoding. En producción se debería integrar un sistema de gestión de secretos como HashiCorp Vault, AWS Secrets Manager o el mecanismo de encriptación en reposo de Kubernetes (`EncryptionConfiguration`).

### 9.3 Imágenes Docker sin Repositorio Externo

Las imágenes (`users-service:1.0`, etc.) se referencian con tag local, lo que asume que el nodo Kubernetes tiene acceso directo a las imágenes construidas localmente (política `imagePullPolicy: IfNotPresent`). En un clúster multi-nodo real, las imágenes deben publicarse en un registro de contenedores (Docker Hub, AWS ECR, GitHub Container Registry).

### 9.4 Sin TLS en el Gateway

El Istio Gateway está configurado en modo HTTP sin TLS. Para un entorno de producción se requeriría HTTPS con un certificado válido gestionado por cert-manager o similar. Sin TLS, las credenciales y datos viajan en claro entre el usuario y el ingress.

### 9.5 Ausencia de Observabilidad Completa

Aunque Istio incluye integraciones con Prometheus, Grafana, Jaeger y Kiali, estos addons no están desplegados en el proyecto. La ausencia de trazabilidad distribuida dificulta el diagnóstico de latencias inter-servicio o errores en cascada.

### 9.6 El Header X-type en Orders es Aleatorio

El microservicio Orders genera el valor de `X-type` de forma aleatoria para las llamadas a Users y Products. Esto significa que el enrutamiento de las llamadas internas desde Orders no es determinista ni controlable desde el exterior. Idealmente, el header recibido por Orders debería propagarse hacia aguas abajo (header propagation).

### 9.7 Ausencia de Circuit Breaker

No se configura ninguna política de tolerancia a fallos (circuit breaker, retry, timeout) en los DestinationRules de Istio. Si Users o Products no responde, Orders fallará sin reintentos automáticos. Istio permite definir estas políticas de forma declarativa, pero no están implementadas en este proyecto.

---

## 10. Conclusiones

El proyecto demuestra satisfactoriamente la implementación de una arquitectura de microservicios de tres niveles sobre Kubernetes con Istio, cumpliendo todos los requisitos del enunciado: mínimo 3 réplicas por servicio, dos versiones por microservicio con respuestas diferenciables, enrutamiento mediante cabecera `X-type`, pruebas A/B, y automatización completa de todas las operaciones mediante scripts de shell. Las debilidades identificadas son propias de un entorno académico y se resolverían en una implantación productiva real.

---

*Documento preparado según las especificaciones del Trabajo Final del Módulo de Cloud Computing.*
