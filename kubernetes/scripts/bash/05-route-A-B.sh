#!/bin/bash
# Activa enrutamiento A/B en Users y Products basado en la cabecera HTTP X-type.
# X-type: v2 → versión 2 | sin cabecera (o cualquier otro valor) → versión 1.

NAMESPACE=cloud

echo "Applying A/B VirtualServices (X-type header routing)"
for i in $(ls ../../istio/virtual-service/A-B/) ; do kubectl apply -n $NAMESPACE -f ../../istio/virtual-service/A-B/$i ; done
