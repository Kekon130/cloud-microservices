#Requires -Version 5.1
# Rollback: vuelve todos los microservicios a la versión 1.
# Istio actualiza el enrutamiento de forma inmediata; Kubernetes hace rolling update de los pods.

$Namespace = "cloud"

Write-Host "Restoring all VirtualServices to v1..." -ForegroundColor Cyan
Get-ChildItem "..\..\istio\virtual-service\v1\*.yaml" | ForEach-Object {
    kubectl apply -n $Namespace -f $_.FullName
}

Write-Host "Rolling back Users Microservice to v1..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\users-microservice\v1\users-deployment.yaml"

Write-Host "Rolling back Products Microservice to v1..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\products-microservice\v1\products-deployment.yaml"

Write-Host "Rolling back Orders Microservice to v1..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\orders-microservice\v1\orders-deployment.yaml"

Write-Host "Rollback complete. All services restored to v1." -ForegroundColor Green
kubectl get pods -n $Namespace
kubectl get virtualservices -n $Namespace
