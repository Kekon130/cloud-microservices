#Requires -Version 5.1
# Despliega la versión 2 únicamente en Users y Products. Orders permanece en v1.

$Namespace = "cloud"

Write-Host "Updating VirtualServices to v2 (Users and Products)..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\istio\virtual-service\v2\users-virtualservice.yaml"
kubectl apply -n $Namespace -f "..\..\istio\virtual-service\v2\products-virtualservice.yaml"
kubectl apply -n $Namespace -f "..\..\istio\virtual-service\partial-v2\client-virtualservice.yaml"

Write-Host "Deploying Users Microservice v2..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\users-microservice\v2\users-deployment.yaml"

Write-Host "Deploying Products Microservice v2..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\products-microservice\v2\products-deployment.yaml"

Write-Host "Done. Orders remains on v1." -ForegroundColor Green
kubectl get pods -n $Namespace -l "app in (users-microservice,products-microservice)"
