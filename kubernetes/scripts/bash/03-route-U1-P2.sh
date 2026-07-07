#!/bin/bash
# Enruta tráfico de Users a v1 y Products a v2.

NAMESPACE=cloud

echo "Routing: Users → v1, Products → v2"
kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/v1/users-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/v2/products-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/U1-P2/client-virtualservice.yaml
