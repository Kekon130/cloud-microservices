#!/bin/bash
# Despliega la versión 2 de todos los microservicios y actualiza todos los VirtualServices.

NAMESPACE=cloud

echo "Updating all VirtualServices to v2"
for i in $(ls ../../istio/virtual-service/v2/) ; do kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/v2/$i ; done

echo "Deploying Users Microservice v2"
kubectl apply -n $NAMESPACE -f ../../users-microservice/v2/users-deployment.yaml

echo "Deploying Products Microservice v2"
kubectl apply -n $NAMESPACE -f ../../products-microservice/v2/products-deployment.yaml

echo "Deploying Orders Microservice v2"
kubectl apply -n $NAMESPACE -f ../../orders-microservice/v2/orders-deployment.yaml
