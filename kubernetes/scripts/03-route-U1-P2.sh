#!/bin/bash

NAMESPACE=cloud

echo "Creating VirtualServices"
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/v1/users-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/v2/products-virtualservice.yaml