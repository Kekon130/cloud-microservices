#!/bin/bash
# Verifica el enrutamiento de versiones a través del Istio Ingress Gateway.
# Ejecutar DESPUÉS de aplicar los VirtualServices A/B (script 05-route-A-B.sh).

NAMESPACE=cloud

GATEWAY_IP=$(kubectl -n istio-system get service istio-ingressgateway \
  -o jsonpath='{.status.loadBalancer.ingress[0].ip}' 2>/dev/null)

if [ -z "$GATEWAY_IP" ]; then
  # En entornos sin LoadBalancer (minikube/kind) usar NodePort
  GATEWAY_IP=$(kubectl get nodes -o jsonpath='{.items[0].status.addresses[0].address}')
  GATEWAY_PORT=$(kubectl -n istio-system get service istio-ingressgateway \
    -o jsonpath='{.spec.ports[?(@.name=="http2")].nodePort}')
  GATEWAY_URL="http://${GATEWAY_IP}:${GATEWAY_PORT}"
else
  GATEWAY_URL="http://${GATEWAY_IP}"
fi

echo "Gateway URL: $GATEWAY_URL"
echo ""

echo "=== TEST 1: Sin cabecera X-type → debe responder v1 ==="
curl -s "$GATEWAY_URL/users/id/1" | python3 -m json.tool
echo ""

echo "=== TEST 2: Con X-type: v2 → debe responder v2 ==="
curl -s -H "X-type: v2" "$GATEWAY_URL/users/id/1" | python3 -m json.tool
echo ""

echo "=== TEST 3: Con X-type: v1 → debe responder v1 ==="
curl -s -H "X-type: v1" "$GATEWAY_URL/users/id/1" | python3 -m json.tool
echo ""

echo "=== TEST 4: 10 llamadas sin cabecera (todas deben ser v1) ==="
OK=0
for i in $(seq 1 10); do
  VERSION=$(curl -s "$GATEWAY_URL/users/id/1" | grep -o '"version":"[^"]*"')
  echo "  Llamada $i: $VERSION"
  if echo "$VERSION" | grep -q "v1"; then OK=$((OK+1)); fi
done
echo "  Resultado: $OK/10 respuestas en v1"
echo ""

echo "=== TEST 5: 10 llamadas con X-type: v2 (todas deben ser v2) ==="
OK=0
for i in $(seq 1 10); do
  VERSION=$(curl -s -H "X-type: v2" "$GATEWAY_URL/users/id/1" | grep -o '"version":"[^"]*"')
  echo "  Llamada $i: $VERSION"
  if echo "$VERSION" | grep -q "v2"; then OK=$((OK+1)); fi
done
echo "  Resultado: $OK/10 respuestas en v2"
echo ""

echo "=== TEST 6: Products — misma lógica A/B ==="
echo "  Sin cabecera:"
curl -s "$GATEWAY_URL/products/id/1" | python3 -m json.tool
echo "  Con X-type: v2:"
curl -s -H "X-type: v2" "$GATEWAY_URL/products/id/1" | python3 -m json.tool
