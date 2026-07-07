#Requires -Version 5.1
# Genera carga sobre users-microservice y observa el escalado automático (HPA).
# Uso: .\09-test-load.ps1 [-Duration 60]

param(
    [int]$Duration = 60
)

$Namespace = "cloud"
$PodName   = "load-test-ps"

Write-Host "Iniciando test de carga durante ${Duration}s..." -ForegroundColor Cyan
Write-Host "Observa el HPA en otra terminal con:"
Write-Host "  kubectl get hpa -n $Namespace -w"
Write-Host ""

# Eliminar pod previo si existe
kubectl delete pod $PodName -n $Namespace --ignore-not-found=true 2>$null | Out-Null

# Manifiesto del pod de carga
$PodYaml = @"
apiVersion: v1
kind: Pod
metadata:
  name: $PodName
  namespace: $Namespace
spec:
  restartPolicy: Never
  containers:
  - name: load
    image: busybox
    command:
    - sh
    - -c
    - |
      END=\$(( \$(date +%s) + $Duration ))
      COUNT=0
      while [ \$(date +%s) -lt \$END ]; do
        wget -q -O- http://users-microservice-service:8000/users > /dev/null 2>&1
        COUNT=\$((COUNT+1))
      done
      echo "Total peticiones enviadas: \$COUNT"
"@

$PodYaml | kubectl apply -f - 2>&1 | Out-Null
Write-Host "Pod de carga '$PodName' lanzado." -ForegroundColor Green
Write-Host "Monitorizando HPA cada 5 segundos..." -ForegroundColor Cyan
Write-Host ""

$EndTime = (Get-Date).AddSeconds($Duration)
while ((Get-Date) -lt $EndTime) {
    Write-Host ("--- {0:HH:mm:ss} ---" -f (Get-Date)) -ForegroundColor DarkGray
    $hpaLines = kubectl get hpa -n $Namespace --no-headers 2>$null
    if ($hpaLines) {
        foreach ($line in $hpaLines) { Write-Host "  $line" }
    } else {
        Write-Host "  (sin HPAs disponibles)" -ForegroundColor Yellow
    }
    Start-Sleep -Seconds 5
}

Write-Host ""
Write-Host "Test de carga finalizado." -ForegroundColor Green
Write-Host ""

Write-Host "Resultado del pod de carga:" -ForegroundColor Cyan
kubectl logs $PodName -n $Namespace 2>$null

Write-Host ""
Write-Host "Estado final de pods users-microservice:" -ForegroundColor Cyan
kubectl get pods -n $Namespace -l app=users-microservice

kubectl delete pod $PodName -n $Namespace --ignore-not-found=true 2>$null | Out-Null
Write-Host "Pod de carga eliminado." -ForegroundColor DarkGray
