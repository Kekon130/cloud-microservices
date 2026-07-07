#Requires -Version 5.1
# Enruta tráfico de Users a v2 y Products a v1.

$Namespace = "cloud"

Write-Host "Routing: Users → v2, Products → v1" -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\istio\virtual-service\v2\users-virtualservice.yaml"
kubectl apply -n $Namespace -f "..\..\istio\virtual-service\v1\products-virtualservice.yaml"
kubectl apply -n $Namespace -f "..\..\istio\virtual-service\U2-P1\client-virtualservice.yaml"

Write-Host "Done." -ForegroundColor Green
kubectl get virtualservices -n $Namespace
