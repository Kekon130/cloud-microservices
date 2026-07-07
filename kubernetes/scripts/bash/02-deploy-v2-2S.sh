#!/bin/bash
# Despliega la versión 2 únicamente en Users y Products. Orders permanece en v1.

NAMESPACE=cloud

echo "Updating VirtualServices to v2 (Users and Products)"
kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/v2/users-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/v2/products-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/partial-v2/client-virtualservice.yaml

echo "Deploying Users Microservice v2"
kubectl apply -n $NAMESPACE -f ../../users-microservice/v2/users-deployment.yaml

echo "Deploying Products Microservice v2"
kubectl apply -n $NAMESPACE -f ../../products-microservice/v2/products-deployment.yaml
