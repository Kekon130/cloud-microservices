#Requires -Version 5.1
# Enruta tráfico de Users a v1 y Products a v2.

$Namespace = "cloud"

Write-Host "Routing: Users → v1, Products → v2" -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\istio\virtual-service\v1\users-virtualservice.yaml"
kubectl apply -n $Namespace -f "..\..\istio\virtual-service\v2\products-virtualservice.yaml"
kubectl apply -n $Namespace -f "..\..\istio\virtual-service\U1-P2\client-virtualservice.yaml"

Write-Host "Done." -ForegroundColor Green
kubectl get virtualservices -n $Namespace
