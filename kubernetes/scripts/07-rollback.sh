#!/bin/bash

NAMESPACE=cloud

echo "Creating VirtualServices"
for i in $(ls ../istio/virtual-service/v1/) ; do kubectl apply -n $NAMESPACE -f ../istio/virtual-service/v1/$i ; done

echo "Deploying Users Microservice"
kubectl apply -n $NAMESPACE -f ../users-microservice/v1/users-deployment.yaml

echo "Deploying Products Microservice"
kubectl apply -n $NAMESPACE -f ../products-microservice/v1/products-deployment.yaml

echo "Deploying Orders Microservice"
kubectl apply -n $NAMESPACE -f ../orders-microservice/v1/orders-deployment.yaml