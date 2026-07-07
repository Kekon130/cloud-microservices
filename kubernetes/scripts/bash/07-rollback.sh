#!/bin/bash
# Rollback: vuelve todos los microservicios a la versión 1.
# Istio actualiza el enrutamiento de forma inmediata; Kubernetes hace rolling update de los pods.

NAMESPACE=cloud

echo "Restoring all VirtualServices to v1"
for i in $(ls ../../istio/virtual-service/v1/) ; do kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/v1/$i ; done

echo "Rolling back Users Microservice to v1"
kubectl apply -n $NAMESPACE -f ../../users-microservice/v1/users-deployment.yaml

echo "Rolling back Products Microservice to v1"
kubectl apply -n $NAMESPACE -f ../../products-microservice/v1/products-deployment.yaml

echo "Rolling back Orders Microservice to v1"
kubectl apply -n $NAMESPACE -f ../../orders-microservice/v1/orders-deployment.yaml
