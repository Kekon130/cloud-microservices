#Requires -Version 5.1
# Activa enrutamiento A/B en Users y Products basado en la cabecera HTTP X-type.
# X-type: v2 → versión 2 | sin cabecera (o cualquier otro valor) → versión 1.

$Namespace = "cloud"

Write-Host "Applying A/B VirtualServices (X-type header routing)..." -ForegroundColor Cyan
Get-ChildItem "..\..\istio\virtual-service\A-B\*.yaml" | ForEach-Object {
    kubectl apply -n $Namespace -f $_.FullName
}

Write-Host "Done. Use header 'X-type: v2' to reach version 2." -ForegroundColor Green
kubectl get virtualservices -n $Namespace
