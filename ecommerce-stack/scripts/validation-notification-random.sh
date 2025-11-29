#!/bin/bash

echo "============================================"
echo "🎲 TESTE ALEATÓRIO: APROVADO ou FALHO"
echo "============================================"

#
# 🔢 Seleção do USER_ID com 50% de probabilidade para 999
#

RAND_BYTE=$(od -An -N1 -i /dev/urandom | tr -d ' ')

if (( RAND_BYTE % 2 == 0 )); then
  USER_ID=999
else
  USER_ID=$((RANDOM % 9000 + 1000))
fi

RANDOM_SUFFIX=$((RANDOM % 9999))

echo "👉 USER_ID selecionado para o teste: $USER_ID"

#
# Função para validar serviços
#
check_service() {
  URL=$1
  NAME=$2

  if curl -s -o /dev/null -w "%{http_code}" "$URL" | grep -q "200"; then
    echo "✔ $NAME OK"
  else
    echo "❌ $NAME NÃO RESPONDE → $URL"
    exit 1
  fi
}

echo
echo "🔍 Validando serviços..."

check_service "http://localhost:8080/actuator/health" "gateway-service"
check_service "http://localhost:8081/actuator/health" "product-service"
check_service "http://localhost:8082/actuator/health" "inventory-service"
check_service "http://localhost:8083/actuator/health" "cart-service"
check_service "http://localhost:8084/actuator/health" "order-service"
check_service "http://localhost:8085/actuator/health" "payment-service"
check_service "http://localhost:8086/actuator/health" "notification-service"

#
# Criar produto
#
echo
echo "🔵 Criando produto..."
PRODUCT_JSON=$(curl -s -X POST http://localhost:8080/products \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Produto Teste $RANDOM_SUFFIX\",\"description\":\"Simulação stochastic\",\"price\":1}")

echo "$PRODUCT_JSON" | jq . 2>/dev/null || echo "$PRODUCT_JSON"
PRODUCT_ID=$(echo "$PRODUCT_JSON" | jq -r '.id')
echo "➡️ Produto criado com ID: $PRODUCT_ID"

#
# Ajustar estoque
#
echo
echo "🔧 Ajustando estoque..."
STOCK=$(curl -s -X POST http://localhost:8080/inventory/increase/$PRODUCT_ID/5)
echo "$STOCK" | jq . 2>/dev/null || echo "$STOCK"

#
# Adicionar item ao carrinho
#
echo
echo "🛒 Adicionando item ao carrinho..."
CART_ADD=$(curl -s -X POST http://localhost:8080/cart/$USER_ID/items \
  -H "Content-Type: application/json" \
  -d "{\"productId\": $PRODUCT_ID, \"quantity\": 1}")

echo "$CART_ADD" | jq . 2>/dev/null || echo "$CART_ADD"

#
# Consultar carrinho
#
echo
echo "🛒 Consultando carrinho..."
CART=$(curl -s http://localhost:8080/cart/$USER_ID)
echo "$CART" | jq . 2>/dev/null || echo "$CART"

#
# Criar pedido
#
echo
echo "🧾 Criando pedido..."
ORDER_JSON=$(curl -s -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d "{\"userId\": $USER_ID, \"items\": [{\"productId\": $PRODUCT_ID, \"quantity\": 1}]}")

echo "$ORDER_JSON" | jq . 2>/dev/null || echo "$ORDER_JSON"
ORDER_ID=$(echo "$ORDER_JSON" | jq -r '.orderId')

echo "➡️ Pedido criado com ID: $ORDER_ID"

#
# Aguardar processamento
#
echo
echo "⏳ Aguardando processamento..."
sleep 4

#
# Consultar status final
#
echo
echo "🧾 Status final do pedido:"
STATUS=$(curl -s http://localhost:8080/orders/$ORDER_ID)
echo "$STATUS" | jq . 2>/dev/null || echo "$STATUS"

echo
echo "============================================"
echo "🏁 TESTE FINALIZADO (RESULTADO ALEATÓRIO)"
echo "============================================"
