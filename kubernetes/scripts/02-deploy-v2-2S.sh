#!/bin/bash

NAMESPACE=cloud

echo "Creating VirtualServices"
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/v2/users-virtualservice.yaml
kubectl apply -n $NAMESPACE -f ../istio/virtual-service/v2/products-virtualservice.yaml

echo "Deploying Users Microservice"
kubectl apply -n $NAMESPACE -f ../users-microservice/v2/users-deployment.yaml

echo "Deploying Products Microservice"
kubectl apply -n $NAMESPACE -f ../products-microservice/v2/products-deployment.yaml