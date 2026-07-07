#!/bin/bash
# Enruta tráfico de Users a v2 y Products a v1.

NAMESPACE=cloud

echo "Routing: Users → v2, Products → v1"
kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/v2/users-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/v1/products-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/U2-P1/client-virtualservice.yaml
