#!/bin/bash
# Genera carga sobre users-microservice y observa el escalado automático (HPA).
# Uso: bash 09-test-load.sh [segundos]   (por defecto 60)

NAMESPACE=cloud
DURATION=${1:-60}

echo "Iniciando test de carga durante ${DURATION}s..."
echo "Observa el HPA en otra terminal con:"
echo "  kubectl get hpa -n $NAMESPACE -w"
echo ""

kubectl run -n $NAMESPACE load-test \
  --image=busybox \
  --restart=Never \
  --rm \
  -it \
  --timeout="${DURATION}s" \
  -- sh -c "
    END=\$((\$(date +%s) + $DURATION))
    COUNT=0
    while [ \$(date +%s) -lt \$END ]; do
      wget -q -O- http://users-microservice-service:8000/users > /dev/null 2>&1
      COUNT=\$((COUNT+1))
    done
    echo \"Total peticiones enviadas: \$COUNT\"
  " &

LOAD_PID=$!

echo "Pod de carga lanzado (PID local: $LOAD_PID)"
echo "Monitorizando HPA cada 5 segundos durante ${DURATION}s..."
echo ""

END=$(($(date +%s) + DURATION))
while [ $(date +%s) -lt $END ]; do
  echo "--- $(date '+%H:%M:%S') ---"
  kubectl get hpa -n $NAMESPACE --no-headers \
    -o custom-columns="NAME:.metadata.name,MIN:.spec.minReplicas,MAX:.spec.maxReplicas,CURRENT:.status.currentReplicas"
  sleep 5
done

wait $LOAD_PID 2>/dev/null
echo ""
echo "Test de carga finalizado."
echo ""
echo "Estado final de los pods:"
kubectl get pods -n $NAMESPACE -l app=users-microservice
