#Requires -Version 5.1
# Verifica el enrutamiento de versiones a través del Istio Ingress Gateway.
# Ejecutar DESPUÉS de aplicar los VirtualServices A/B (script 05-route-A-B.ps1).

$Namespace = "cloud"

# ── Obtener URL del Gateway ────────────────────────────────────────────────────
$GatewayIp = kubectl -n istio-system get service istio-ingressgateway `
    -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>$null

if ([string]::IsNullOrWhiteSpace($GatewayIp)) {
    $GatewayIp   = kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}'
    $GatewayPort = kubectl -n istio-system get service istio-ingressgateway `
        -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}'
    $GatewayUrl  = "http://${GatewayIp}:${GatewayPort}"
} else {
    $GatewayUrl = "http://$GatewayIp"
}

Write-Host "Gateway URL: $GatewayUrl" -ForegroundColor Cyan
Write-Host ""

function Invoke-ServiceCall {
    param([string]$Url, [hashtable]$Headers = @{})
    try {
        return Invoke-RestMethod -Uri $Url -Headers $Headers -Method Get -TimeoutSec 10
    } catch {
        Write-Warning "Error en $Url : $_"
        return $null
    }
}

function Show-Response {
    param($Response)
    if ($null -eq $Response) { Write-Host "  (sin respuesta)" -ForegroundColor Red }
    else { $Response | ConvertTo-Json -Depth 5 }
}

# ── Test 1 ────────────────────────────────────────────────────────────────────
Write-Host "=== TEST 1: Sin cabecera X-type → debe responder v1 ===" -ForegroundColor Yellow
Show-Response (Invoke-ServiceCall "$GatewayUrl/users/id/1")
Write-Host ""

# ── Test 2 ────────────────────────────────────────────────────────────────────
Write-Host "=== TEST 2: Con X-type: v2 → debe responder v2 ===" -ForegroundColor Yellow
Show-Response (Invoke-ServiceCall "$GatewayUrl/users/id/1" -Headers @{"X-type" = "v2"})
Write-Host ""

# ── Test 3 ────────────────────────────────────────────────────────────────────
Write-Host "=== TEST 3: Con X-type: v1 → debe responder v1 ===" -ForegroundColor Yellow
Show-Response (Invoke-ServiceCall "$GatewayUrl/users/id/1" -Headers @{"X-type" = "v1"})
Write-Host ""

# ── Test 4: 10 llamadas sin cabecera ──────────────────────────────────────────
Write-Host "=== TEST 4: 10 llamadas sin cabecera (todas deben ser v1) ===" -ForegroundColor Yellow
$ok = 0
1..10 | ForEach-Object {
    $r   = Invoke-ServiceCall "$GatewayUrl/users/id/1"
    $ver = if ($r) { $r.version } else { "ERROR" }
    Write-Host ("  Llamada {0,2}: version = {1}" -f $_, $ver)
    if ($ver -eq "v1") { $ok++ }
}
$color = if ($ok -eq 10) { "Green" } else { "Red" }
Write-Host ("  Resultado: {0}/10 respuestas en v1" -f $ok) -ForegroundColor $color
Write-Host ""

# ── Test 5: 10 llamadas con X-type: v2 ───────────────────────────────────────
Write-Host "=== TEST 5: 10 llamadas con X-type: v2 (todas deben ser v2) ===" -ForegroundColor Yellow
$ok = 0
1..10 | ForEach-Object {
    $r   = Invoke-ServiceCall "$GatewayUrl/users/id/1" -Headers @{"X-type" = "v2"}
    $ver = if ($r) { $r.version } else { "ERROR" }
    Write-Host ("  Llamada {0,2}: version = {1}" -f $_, $ver)
    if ($ver -eq "v2") { $ok++ }
}
$color = if ($ok -eq 10) { "Green" } else { "Red" }
Write-Host ("  Resultado: {0}/10 respuestas en v2" -f $ok) -ForegroundColor $color
Write-Host ""

# ── Test 6: Products ──────────────────────────────────────────────────────────
Write-Host "=== TEST 6: Products — misma lógica A/B ===" -ForegroundColor Yellow
Write-Host "  Sin cabecera:"
Show-Response (Invoke-ServiceCall "$GatewayUrl/products/id/1")
Write-Host "  Con X-type: v2:"
Show-Response (Invoke-ServiceCall "$GatewayUrl/products/id/1" -Headers @{"X-type" = "v2"})
