#!/bin/bash

# ============================================
# DETECÇÃO AUTOMÁTICA DE AMBIENTE
# ============================================

getent hosts ecommerce-gateway >/dev/null 2>&1
INSIDE_DOCKER=$?

if [ "$INSIDE_DOCKER" -eq 0 ]; then
  echo "🌐 Ambiente detectado: DOCKER NETWORK"
  HOST_GATEWAY="ecommerce-gateway"
  HOST_PRODUCT="product-service"
  HOST_INVENTORY="inventory-service"
  HOST_CART="cart-service"
  HOST_ORDER="order-service"
  HOST_PAYMENT="payment-service"
  HOST_NOTIFICATION="notification-service"
else
  echo "💻 Ambiente detectado: LOCALHOST"
  HOST_GATEWAY="localhost"
  HOST_PRODUCT="localhost"
  HOST_INVENTORY="localhost"
  HOST_CART="localhost"
  HOST_ORDER="localhost"
  HOST_PAYMENT="localhost"
  HOST_NOTIFICATION="localhost"
fi

PORT_GATEWAY=8080
PORT_PRODUCT=8081
PORT_INVENTORY=8082
PORT_CART=8083
PORT_ORDER=8084
PORT_PAYMENT=8085
PORT_NOTIFICATION=8086

echo "============================================"
echo "🎲 TESTE ALEATÓRIO: APROVADO ou FALHO"
echo "============================================"
echo "🔧 Gateway:      http://$HOST_GATEWAY:$PORT_GATEWAY"
echo "🔧 Product:      http://$HOST_PRODUCT:$PORT_PRODUCT"
echo "🔧 Inventory:    http://$HOST_INVENTORY:$PORT_INVENTORY"
echo "🔧 Cart:         http://$HOST_CART:$PORT_CART"
echo "🔧 Order:        http://$HOST_ORDER:$PORT_ORDER"
echo "🔧 Payment:      http://$HOST_PAYMENT:$PORT_PAYMENT"
echo "🔧 Notification: http://$HOST_NOTIFICATION:$PORT_NOTIFICATION"
echo

# ============================================
# Seleção aleatória de ID de usuário
# ============================================

RAND_BYTE=$(od -An -N1 -i /dev/urandom | tr -d ' ')

if (( RAND_BYTE % 2 == 0 )); then
  USER_ID=999
else
  USER_ID=$((RANDOM % 9000 + 1000))
fi

RANDOM_SUFFIX=$((RANDOM % 9999))

echo "👉 USER_ID selecionado para o teste: $USER_ID"
echo

# ============================================
# Função para validar serviços
# ============================================

check_service() {
  URL=$1
  NAME=$2

  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" "$URL")
  if [ "$HTTP_CODE" == "200" ]; then
    echo "✔ $NAME OK"
  else
    echo "❌ $NAME NÃO RESPONDE → $URL (HTTP $HTTP_CODE)"
    exit 1
  fi
}

echo "🔍 Validando serviços..."
check_service "http://$HOST_GATEWAY:$PORT_GATEWAY/actuator/health" "gateway-service"
check_service "http://$HOST_PRODUCT:$PORT_PRODUCT/actuator/health" "product-service"
check_service "http://$HOST_INVENTORY:$PORT_INVENTORY/actuator/health" "inventory-service"
check_service "http://$HOST_CART:$PORT_CART/actuator/health" "cart-service"
check_service "http://$HOST_ORDER:$PORT_ORDER/actuator/health" "order-service"
check_service "http://$HOST_PAYMENT:$PORT_PAYMENT/actuator/health" "payment-service"
check_service "http://$HOST_NOTIFICATION:$PORT_NOTIFICATION/actuator/health" "notification-service"

# ============================================
# Criar produto via Gateway
# ============================================

echo
echo "🔵 Criando produto..."
PRODUCT_JSON=$(curl -s -X POST http://$HOST_GATEWAY:$PORT_GATEWAY/products \
  -H "Content-Type: application/json" \
  -d "{\"name\":\"Produto Teste $RANDOM_SUFFIX\",\"description\":\"Simulação stochastic\",\"price\":1}")

echo "$PRODUCT_JSON" | jq . 2>/dev/null || echo "$PRODUCT_JSON"

PRODUCT_ID=$(echo "$PRODUCT_JSON" | jq -r '.id' 2>/dev/null)

if [ -z "$PRODUCT_ID" ] || [ "$PRODUCT_ID" == "null" ]; then
  echo "❌ Falha ao criar produto. ID inválido."
  exit 1
else
  echo "➡️ Produto criado com ID: $PRODUCT_ID"
fi

# ============================================
# Ajustar estoque (RESTOCK + JSON payload)
# ============================================

echo
echo "🔧 Ajustando estoque (RESTOCK + JSON payload)..."

STOCK=$(curl -s -X POST \
  http://$HOST_GATEWAY:$PORT_GATEWAY/inventory/$PRODUCT_ID/increase \
  -H "Content-Type: application/json" \
  -d "{\"amount\":5, \"reason\":\"RESTOCK\"}")

if echo "$STOCK" | grep -qi "erro"; then
    echo "❌ Falha ao ajustar estoque:"
    echo "$STOCK"
else
    echo "✔ Estoque ajustado"
    echo "$STOCK" | jq . 2>/dev/null || echo "$STOCK"
fi

# ============================================
# CONSULTAR ESTOQUE ATUALIZADO AQUI
# ============================================

echo
echo "📦 Consultando estoque atual do produto $PRODUCT_ID..."
INVENTORY_CHECK=$(curl -s http://$HOST_GATEWAY:$PORT_GATEWAY/inventory/$PRODUCT_ID)
echo "$INVENTORY_CHECK" | jq . 2>/dev/null || echo "$INVENTORY_CHECK"

# ============================================
# Adicionar item ao carrinho
# ============================================

echo
echo "🛒 Adicionando item ao carrinho..."
CART_ADD=$(curl -s -X POST http://$HOST_GATEWAY:$PORT_GATEWAY/cart/$USER_ID/items \
  -H "Content-Type: application/json" \
  -d "{\"productId\": $PRODUCT_ID, \"quantity\": 1}")

echo "$CART_ADD" | jq . 2>/dev/null || echo "$CART_ADD"

# ============================================
# Consultar carrinho
# ============================================

echo
echo "🛒 Consultando carrinho..."
CART=$(curl -s http://$HOST_GATEWAY:$PORT_GATEWAY/cart/$USER_ID)
echo "$CART" | jq . 2>/dev/null || echo "$CART"

# ============================================
# Criar pedido
# ============================================

echo
echo "🧾 Criando pedido..."
ORDER_JSON=$(curl -s -X POST http://$HOST_GATEWAY:$PORT_GATEWAY/orders \
  -H "Content-Type: application/json" \
  -d "{\"userId\": $USER_ID, \"items\": [{\"productId\": $PRODUCT_ID, \"quantity\": 1}]}")

echo "$ORDER_JSON" | jq . 2>/dev/null || echo "$ORDER_JSON"
ORDER_ID=$(echo "$ORDER_JSON" | jq -r '.orderId')

echo "➡️ Pedido criado com ID: $ORDER_ID"

# ============================================
# Esperar processamento
# ============================================

echo
echo "⏳ Aguardando processamento..."
sleep 4

# ============================================
# Consultar status final
# ============================================

echo
echo "🧾 Status final do pedido:"
STATUS=$(curl -s http://$HOST_GATEWAY:$PORT_GATEWAY/orders/$ORDER_ID)
echo "$STATUS" | jq . 2>/dev/null || echo "$STATUS"

echo
echo "============================================"
echo "🏁 TESTE FINALIZADO (RESULTADO ALEATÓRIO)"
echo "============================================"
