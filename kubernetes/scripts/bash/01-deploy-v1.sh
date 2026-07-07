#!/bin/bash

NAMESPACE=cloud

echo "Checking if the namespace cloud exists"
if kubectl get namespace cloud > /dev/null 2>&1; then
  echo "Namespace cloud already exists"
else
  echo "Namespace cloud does not exist. Creating..."
  kubectl create namespace cloud
  kubectl label namespace cloud istio-injection=enabled
fi

echo "Verifying istio is installed"
istioctl install --set profile=demo -y

echo "Creating Istio Gateway"
kubectl apply -n $NAMESPACE -f ../../istio/gateway.yaml

echo "Creating Destination Rules"
for i in $(ls ../../istio/destination-rules/) ; do kubectl apply -n $NAMESPACE -f ../../istio/destination-rules/$i ; done

echo "Creating VirtualServices"
for i in $(ls ../../istio/virtual-service/v1/) ; do kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/v1/$i ; done

echo "Creating secrets for DB"
kubectl create secret generic cloud-db-schema --from-file ../../../db/scripts/01_set_up.sql -n $NAMESPACE
kubectl create secret generic cloud-db-init   --from-file ../../../db/scripts/02_db_ini.sql  -n $NAMESPACE

echo "Deploying Database"
kubectl apply -n $NAMESPACE \
  -f ../../database/mysql-secret.yaml \
  -f ../../database/mysql-pvc.yaml \
  -f ../../database/mysql-service.yaml \
  -f ../../database/mysql-deployment.yaml

echo "Deploying Users Microservice"
kubectl apply -n $NAMESPACE \
  -f ../../users-microservice/users-db-secrets.yaml \
  -f ../../users-microservice/users-service.yaml \
  -f ../../users-microservice/v1/users-deployment.yaml

echo "Deploying Products Microservice"
kubectl apply -n $NAMESPACE \
  -f ../../products-microservice/products-db-secrets.yaml \
  -f ../../products-microservice/products-service.yaml \
  -f ../../products-microservice/v1/products-deployment.yaml

echo "Deploying Orders Microservice"
kubectl apply -n $NAMESPACE \
  -f ../../orders-microservice/orders-db-secrets.yaml \
  -f ../../orders-microservice/orders-config.yaml \
  -f ../../orders-microservice/orders-service.yaml \
  -f ../../orders-microservice/v1/orders-deployment.yaml

echo "Deploying Web Client"
kubectl apply -n $NAMESPACE \
  -f ../../client/client-config.yaml \
  -f ../../client/client-service.yaml \
  -f ../../client/client-deployment.yaml

#echo "Deploying HorizontalPodAutoscalers"
#for i in $(ls ../../hpa/) ; do kubectl apply -n $NAMESPACE -f ../../hpa/$i ; done
