#!/bin/bash

NAMESPACE=cloud

echo "Creating VirtualServices"
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/A-B/users-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/A-B/products-virtualservice.yaml