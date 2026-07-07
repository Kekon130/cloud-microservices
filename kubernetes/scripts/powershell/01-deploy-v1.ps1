#Requires -Version 5.1
# Despliegue completo de la aplicación en versión 1.

$Namespace = "cloud"

# ── Namespace ─────────────────────────────────────────────────────────────────
Write-Host "Checking if namespace '$Namespace' exists..." -ForegroundColor Cyan
$nsExists = kubectl get namespace $Namespace 2>$null
if ($LASTEXITCODE -eq 0) {
    Write-Host "Namespace '$Namespace' already exists." -ForegroundColor Green
} else {
    Write-Host "Creating namespace '$Namespace'..." -ForegroundColor Yellow
    kubectl create namespace $Namespace
    kubectl label namespace $Namespace istio-injection=enabled
}

# ── Istio ──────────────────────────────────────────────────────────────────────
Write-Host "Installing Istio (demo profile)..." -ForegroundColor Cyan
istioctl install --set profile=demo -y

# ── Gateway ───────────────────────────────────────────────────────────────────
Write-Host "Creating Istio Gateway..." -ForegroundColor Cyan
kubectl apply -n $Namespace -f "..\..\istio\gateway.yaml"

# ── Destination Rules ─────────────────────────────────────────────────────────
Write-Host "Creating Destination Rules..." -ForegroundColor Cyan
Get-ChildItem "..\..\istio\destination-rules\*.yaml" | ForEach-Object {
    kubectl apply -n $Namespace -f $_.FullName
}

# ── VirtualServices v1 ────────────────────────────────────────────────────────
Write-Host "Creating VirtualServices (v1)..." -ForegroundColor Cyan
Get-ChildItem "..\..\istio\virtual-service\v1\*.yaml" | ForEach-Object {
    kubectl apply -n $Namespace -f $_.FullName
}

# ── DB Secrets ────────────────────────────────────────────────────────────────
Write-Host "Creating secrets for DB..." -ForegroundColor Cyan
kubectl create secret generic cloud-db-schema `
    --from-file "..\..\..\db\scripts\01_set_up.sql" -n $Namespace
kubectl create secret generic cloud-db-init `
    --from-file "..\..\..\db\scripts\02_db_ini.sql" -n $Namespace

# ── Database ──────────────────────────────────────────────────────────────────
Write-Host "Deploying Database..." -ForegroundColor Cyan
kubectl apply -n $Namespace `
    -f "..\..\database\mysql-secret.yaml" `
    -f "..\..\database\mysql-pvc.yaml" `
    -f "..\..\database\mysql-service.yaml" `
    -f "..\..\database\mysql-deployment.yaml"

# ── Users ─────────────────────────────────────────────────────────────────────
Write-Host "Deploying Users Microservice..." -ForegroundColor Cyan
kubectl apply -n $Namespace `
    -f "..\..\users-microservice\users-db-secrets.yaml" `
    -f "..\..\users-microservice\users-service.yaml" `
    -f "..\..\users-microservice\v1\users-deployment.yaml"

# ── Products ──────────────────────────────────────────────────────────────────
Write-Host "Deploying Products Microservice..." -ForegroundColor Cyan
kubectl apply -n $Namespace `
    -f "..\..\products-microservice\products-db-secrets.yaml" `
    -f "..\..\products-microservice\products-service.yaml" `
    -f "..\..\products-microservice\v1\products-deployment.yaml"

# ── Orders ────────────────────────────────────────────────────────────────────
Write-Host "Deploying Orders Microservice..." -ForegroundColor Cyan
kubectl apply -n $Namespace `
    -f "..\..\orders-microservice\orders-db-secrets.yaml" `
    -f "..\..\orders-microservice\orders-config.yaml" `
    -f "..\..\orders-microservice\orders-service.yaml" `
    -f "..\..\orders-microservice\v1\orders-deployment.yaml"

# ── Client ────────────────────────────────────────────────────────────────────
Write-Host "Deploying Web Client..." -ForegroundColor Cyan
kubectl apply -n $Namespace `
    -f "..\..\client\client-config.yaml" `
    -f "..\..\client\client-service.yaml" `
    -f "..\..\client\client-deployment.yaml"

# ── HPAs ──────────────────────────────────────────────────────────────────────
#Write-Host "Deploying HorizontalPodAutoscalers..." -ForegroundColor Cyan
#Get-ChildItem "..\..\hpa\*.yaml" | ForEach-Object {
#    kubectl apply -n $Namespace -f $_.FullName
#}

Write-Host "Deployment complete. Checking pod status..." -ForegroundColor Green
kubectl get pods -n $Namespace
