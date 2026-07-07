#Requires -Version 5.1
# Despliega la versión 2 de todos los microservicios y actualiza todos los VirtualServices.

$Namespace = "cloud"

Write-Host "Updating all VirtualServices to v2..." -ForegroundColor Cyan
Get-ChildItem "..\..\istio\virtual-service\v2\*.yaml" | ForEach-Object {
    kubectl apply -n $Namespace -f $_.FullName
}

Write-Host "Deploying Users Microservice v2..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\users-microservice\v2\users-deployment.yaml"

Write-Host "Deploying Products Microservice v2..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\products-microservice\v2\products-deployment.yaml"

Write-Host "Deploying Orders Microservice v2..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\orders-microservice\v2\orders-deployment.yaml"

Write-Host "All services now on v2." -ForegroundColor Green
kubectl get pods -n $Namespace
