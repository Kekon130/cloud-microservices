# Guión del Vídeo — Práctica Final
## Arquitectura Cloud de Microservicios con Kubernetes e Istio
**Duración máxima:** 20 minutos  
**Herramienta:** OBS Studio | **SO:** Windows | **Shell:** PowerShell

---

## ANTES DE GRABAR — Preparación (no grabar esto)

### 1. Arrancar minikube

```powershell
minikube start --cpus=4 --memory=8192 --disk-size=20g --driver=docker
minikube addons enable metrics-server
```

### 2. Apuntar Docker al registro de minikube y construir imágenes

```powershell
minikube docker-env | Invoke-Expression

# Desde la raíz del proyecto
docker build -t users-service:1.0    .\users
docker tag     users-service:1.0     users-service:2.0

docker build -t products-service:1.0 .\products
docker tag     products-service:1.0  products-service:2.0

docker build -t orders-service:1.0   .\orders
docker tag     orders-service:1.0    orders-service:2.0

docker build -t client-service:1.0   .\client
docker tag     client-service:1.0    client-service:2.0
```

### 3. Desplegar v1 y esperar a que todo esté Running

```powershell
cd kubernetes\scripts\powershell
.\01-deploy-v1.ps1

# Esperar hasta ver todos los pods en Running 2/2 (tarda 3-5 min)
kubectl get pods -n cloud -w
```

> Cuando todos estén `Running`, parar el watcher con `Ctrl+C`.

### 4. Abrir Terminal 2 con port-forward (dejar abierta durante toda la grabación)

```powershell
# Terminal 2 — NO cerrar durante la demo
kubectl port-forward -n istio-system svc/istio-ingressgateway 8080:80
```

### 5. En Terminal 1, guardar la URL y probar que responde

```powershell
$GW = "http://localhost:8080"

# Prueba rápida — debe devolver JSON con "version": "v1"
Invoke-RestMethod "$GW/users/id/1"
```

### 6. Configuración de OBS antes de grabar

- [ ] Resolución: 1920×1080
- [ ] Bitrate: ≤ 2000 kbps (para que el archivo quede bajo 100 MB)
- [ ] Fuente de terminal: mínimo 16px
- [ ] Cámara visible en la esquina inferior
- [ ] Notificaciones del sistema desactivadas
- [ ] Terminal 1 (comandos) y Terminal 2 (port-forward) abiertas

---

## DURANTE LA GRABACIÓN

---

### BLOQUE 1 — Introducción y Arquitectura (2 min)

> *Aparecer en cámara. Mostrar la raíz del proyecto en el terminal.*

```powershell
tree . /F /A
```

**Decir:**

"Buenos días. Voy a presentar la práctica final del módulo de Cloud Computing: una arquitectura de microservicios en Kubernetes con gestión de tráfico mediante Istio.

La arquitectura tiene tres niveles. El primero es el front-end: una aplicación web Spring Boot con Thymeleaf. El segundo son los microservicios de negocio: Users, Products y Orders, cada uno con su propio ciclo de vida y dos versiones. El tercero es el almacenamiento: MySQL con usuarios dedicados por microservicio.

Todo el tráfico externo entra por el Istio Ingress Gateway y se distribuye a los microservicios según las reglas de los VirtualServices."

---

### BLOQUE 2 — Código y diferenciación de versiones (2 min)

> *Mostrar la diferencia entre deployment v1 y v2.*

```powershell
Get-Content kubernetes\users-microservice\v1\users-deployment.yaml | Select-String "APP_VERSION" -Context 0,1
Get-Content kubernetes\users-microservice\v2\users-deployment.yaml | Select-String "APP_VERSION" -Context 0,1
```

**Decir:**

"La diferencia entre v1 y v2 se inyecta mediante la variable de entorno `APP_VERSION` en el manifiesto de despliegue. El código la lee y la incluye en todas las respuestas JSON. La misma imagen Docker se comporta diferente según la variable que recibe."

> *Mostrar el mapper donde se inyecta la versión en la respuesta.*

```powershell
Get-Content users\src\main\java\uah\es\users\mapper\UserMapper.java
```

---

### BLOQUE 3 — Operación 1: Despliegue completo v1 (2 min)

> *Mostrar el script y luego el estado actual (ya está desplegado desde la preparación).*

```powershell
Get-Content kubernetes\scripts\powershell\01-deploy-v1.ps1
```

**Decir:**

"El script crea el namespace con inyección de Istio, instala Istio con perfil demo, despliega el Gateway, las DestinationRules, los VirtualServices en versión 1, la base de datos MySQL, los tres microservicios y el cliente web."

Mostrar el estado actual:

```powershell
kubectl get pods -n cloud
```

**Decir:**

"El despliegue ya está corriendo. Para la demo usamos una réplica por servicio para optimizar recursos del entorno local; los manifiestos están preparados para las tres réplicas mínimas que exige el enunciado."

Verificar la respuesta en v1 — ahora funciona directamente desde PowerShell porque el Gateway enruta `/users`, `/products` y `/orders` a los microservicios correspondientes:

```powershell
Invoke-RestMethod "$GW/users/id/1"    | Select-Object id, name, version
Invoke-RestMethod "$GW/products/id/1" | Select-Object id, name, version
Invoke-RestMethod "$GW/orders/id/1"   | Select-Object id, version
```

**Decir:**

"El campo `version: v1` confirma que estamos en la primera versión en todos los servicios. Abrimos también la interfaz web."

```powershell
Start-Process "$GW"
```

---

### BLOQUE 4 — Operación 2: Despliegue v2 en 2 servicios (2 min)

```powershell
Get-Content kubernetes\scripts\powershell\02-deploy-v2-2S.ps1
.\02-deploy-v2-2S.ps1
```

**Decir:**

"Segunda operación: desplegamos v2 solo en Users y Products. Orders sigue en v1. Los VirtualServices de ambos servicios —incluyendo el del Gateway— se actualizan para enrutar a v2."

```powershell
kubectl get pods -n cloud
```

Verificar que los servicios responden en la versión correcta:

```powershell
Invoke-RestMethod "$GW/users/id/1"    | Select-Object id, name, version
Invoke-RestMethod "$GW/products/id/1" | Select-Object id, name, version
Invoke-RestMethod "$GW/orders/id/1"   | Select-Object id, version
```

**Decir:**

"Users y Products responden en v2. Orders sigue en v1. La coexistencia de versiones es completamente transparente para el cliente."

---

### BLOQUE 5 — Operación 3: Enrutamiento a nodos específicos (2 min)

```powershell
Get-Content kubernetes\scripts\powershell\03-route-U1-P2.ps1
.\03-route-U1-P2.ps1
```

**Decir:**

"Tercera operación: enrutamos Users a v1 y Products a v2 modificando únicamente los VirtualServices, sin redeplegar ningún pod."

```powershell
Invoke-RestMethod "$GW/users/id/1"    | Select-Object id, name, version
Invoke-RestMethod "$GW/products/id/1" | Select-Object id, name, version
```

Invertir el enrutamiento:

```powershell
.\04-route-U2-P1.ps1

Invoke-RestMethod "$GW/users/id/1"    | Select-Object id, name, version
Invoke-RestMethod "$GW/products/id/1" | Select-Object id, name, version
```

**Decir:**

"El cambio es instantáneo. Istio actualiza las reglas de enrutamiento en menos de un segundo sin interrumpir el servicio."

---

### BLOQUE 6 — Operación 4: Prueba A/B con cabecera X-type (3 min)

```powershell
Get-Content kubernetes\scripts\powershell\05-route-A-B.ps1
.\05-route-A-B.ps1
```

**Decir:**

"Cuarta operación: prueba A/B. Los VirtualServices —tanto los de malla interna como el del Gateway— inspeccionan la cabecera HTTP `X-type`. Si vale `v2` el tráfico va a la versión 2; en cualquier otro caso va a la versión 1."

Mostrar el VirtualService A/B del Gateway:

```powershell
Get-Content kubernetes\istio\virtual-service\A-B\client-virtualservice.yaml
```

Demostrar en directo:

```powershell
# Sin cabecera → v1
Invoke-RestMethod "$GW/users/id/1" | Select-Object id, name, version

# Con X-type: v2 → v2
Invoke-RestMethod "$GW/users/id/1" -Headers @{"X-type"="v2"} | Select-Object id, name, version
```

Ejecutar el script de testing automático:

```powershell
.\08-test-routing.ps1
```

**Decir:**

"El script hace 10 llamadas sin cabecera y 10 con `X-type: v2` y cuenta los aciertos. El resultado debe ser 10/10 en ambos casos, confirmando que el enrutamiento por cabecera funciona correctamente."

---

### BLOQUE 7 — Operación 5: Despliegue completo v2 (2 min)

```powershell
Get-Content kubernetes\scripts\powershell\06-deploy-v2.ps1
.\06-deploy-v2.ps1
```

**Decir:**

"Quinta operación: desplegamos v2 en todos los servicios, incluido Orders que estaba aún en v1."

```powershell
kubectl get pods -n cloud
```

Verificar que los tres responden en v2:

```powershell
Invoke-RestMethod "$GW/users/id/1"    | Select-Object id, name, version
Invoke-RestMethod "$GW/products/id/1" | Select-Object id, name, version
Invoke-RestMethod "$GW/orders/id/1"   | Select-Object id, version
```

---

### BLOQUE 8 — Operación 6: Rollback a v1 (2 min)

```powershell
Get-Content kubernetes\scripts\powershell\07-rollback.ps1
.\07-rollback.ps1
```

**Decir:**

"Última operación: rollback a v1. El proceso ocurre en dos fases. Primero Istio actualiza las reglas de enrutamiento de forma inmediata: las nuevas peticiones ya van a v1 aunque los pods v2 sigan existiendo. Luego Kubernetes hace el rolling update sustituyendo pods gradualmente sin interrumpir el servicio."

Verificar el enrutamiento inmediato:

```powershell
Invoke-RestMethod "$GW/users/id/1"    | Select-Object id, name, version
Invoke-RestMethod "$GW/products/id/1" | Select-Object id, name, version
Invoke-RestMethod "$GW/orders/id/1"   | Select-Object id, version
```

```powershell
kubectl get pods -n cloud
kubectl get virtualservices -n cloud
```

**Decir:**

"Todos los servicios responden en v1 y los VirtualServices apuntan de nuevo a v1. El rollback ha sido completo."

---

### BLOQUE 9 — Autoscaling con HPA (1 min)

> *No hay HPAs desplegados en esta demo para ahorrar recursos, pero se muestra el YAML.*

```powershell
Get-Content kubernetes\hpa\users-hpa.yaml
```

**Decir:**

"El proyecto incluye HorizontalPodAutoscalers para todos los servicios. En producción, con tres réplicas mínimas, el HPA escalaría automáticamente hasta diez pods cuando el uso de CPU supera el 70%. Para esta demo se ha omitido el despliegue de los HPAs para optimizar los recursos del entorno local, pero los manifiestos están listos para aplicarse."

---

### BLOQUE 10 — Cierre (30 seg)

> *Volver a enfocar la cámara.*

**Decir:**

"Con esto hemos completado las seis operaciones requeridas: despliegue en v1, despliegue parcial de v2, enrutamiento a versiones específicas, prueba A/B con cabecera HTTP, despliegue completo de v2 y rollback. Todo automatizado con scripts de PowerShell sin modificar el código de aplicación en ningún momento.

Muchas gracias."

---

## Chuleta de comandos — tener en Notepad durante la grabación

```powershell
$GW = "http://localhost:8080"

# Verificar versión — los tres servicios
Invoke-RestMethod "$GW/users/id/1"    | Select-Object id, name, version
Invoke-RestMethod "$GW/products/id/1" | Select-Object id, name, version
Invoke-RestMethod "$GW/orders/id/1"   | Select-Object id, version

# Con header X-type
Invoke-RestMethod "$GW/users/id/1" -Headers @{"X-type"="v2"} | Select-Object id, name, version

# Estado del cluster
kubectl get pods -n cloud
kubectl get virtualservices -n cloud

# Si el port-forward cae — re-ejecutar en Terminal 2
kubectl port-forward -n istio-system svc/istio-ingressgateway 8080:80

# Si algo no responde — ver logs
kubectl logs -n cloud -l app=users-microservice -c users-microservice --tail=20
```

---

*Duración estimada: 18-20 minutos*
