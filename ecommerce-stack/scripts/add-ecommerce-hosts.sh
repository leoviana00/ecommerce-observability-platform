#!/bin/bash

HOSTS_FILE="/etc/hosts"

declare -A HOSTS=(
  ["product-service"]="127.0.0.1"
  ["inventory-service"]="127.0.0.1"
  ["cart-service"]="127.0.0.1"
  ["order-service"]="127.0.0.1"
  ["payment-service"]="127.0.0.1"
  ["notification-service"]="127.0.0.1"
  ["ecommerce-gateway"]="127.0.0.1"
)

echo "=== Adicionando Hosts do Ecommerce (se não existirem) ==="

for HOST in "${!HOSTS[@]}"; do
  IP="${HOSTS[$HOST]}"

  if grep -qE "^[0-9.]+\s+$HOST(\s|$)" "$HOSTS_FILE"; then
    echo "✓ $HOST já existe no /etc/hosts"
  else
    echo "→ Adicionando $HOST..."
    echo "$IP $HOST" | sudo tee -a "$HOSTS_FILE" >/dev/null
  fi
done

echo "=== Finalizado ==="
echo
echo "=== Conteúdo atualizado de /etc/hosts ==="
echo

cat "$HOSTS_FILE"
