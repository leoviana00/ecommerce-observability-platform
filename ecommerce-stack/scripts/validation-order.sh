#!/bin/bash

GATEWAY="http://localhost:8080"

echo "============================================"
echo "🧾 TESTES DO ORDER-SERVICE"
echo "============================================"

echo "🔵 1. Criando produto para testes..."
PRODUCT_RESPONSE=$(curl -s -X POST "$GATEWAY/products" \
  -H "Content-Type: application/json" \
  -d '{
        "name": "Teclado Mecânico",
        "description": "Switch blue",
        "price": 350.0,
        "stock": 10
      }')

PRODUCT_ID=$(echo $PRODUCT_RESPONSE | jq -r '.id')

echo "$PRODUCT_RESPONSE"
echo "➡️  Produto criado com ID: $PRODUCT_ID"
echo ""

echo "🔧 2. Ajustando estoque inicial (increase)..."
curl -s -X POST "$GATEWAY/inventory/$PRODUCT_ID/increase" \
  -H "Content-Type: application/json" \
  -d '{
        "amount": 15,
        "reason": "RESTOCK"
      }' > /dev/null

echo "✔ Estoque ajustado!"
echo ""

echo "🔵 3. Consultando estoque atualizado..."
curl -s -X GET "$GATEWAY/inventory/$PRODUCT_ID"
echo ""
echo ""

USER_ID=123
echo "🧑 Usuário de teste: $USER_ID"
echo ""

echo "🛒 4. Criando carrinho com itens..."
CART_RESPONSE=$(curl -s -X POST "$GATEWAY/cart/$USER_ID/items" \
  -H "Content-Type: application/json" \
  -d "{
        \"productId\": $PRODUCT_ID,
        \"quantity\": 2
      }")

echo "$CART_RESPONSE"
echo ""
echo "✔ Item adicionado ao carrinho!"
echo ""

echo "🛒 5. Consultando carrinho..."
curl -s -X GET "$GATEWAY/cart/$USER_ID"
echo ""
echo ""

echo "🧾 6. Criando pedido..."
ORDER_RESPONSE=$(curl -s -X POST "$GATEWAY/orders" \
  -H "Content-Type: application/json" \
  -d "{
        \"userId\": $USER_ID,
        \"items\": [
           { \"productId\": $PRODUCT_ID, \"quantity\": 2 }
        ]
      }")

echo "$ORDER_RESPONSE"
echo ""

ORDER_ID=$(echo "$ORDER_RESPONSE" | jq -r '.orderId')

echo "➡️  Pedido criado com ID: $ORDER_ID"
echo ""

echo "🧾 7. Consultando pedido pelo ID..."
curl -s -X GET "$GATEWAY/orders/$ORDER_ID"
echo ""
echo ""

echo "🔴 8. Testando erro: pedido inexistente..."
curl -s -X GET "$GATEWAY/orders/999999"
echo ""
echo ""

echo "🔴 9. Testando erro: pedido sem itens..."
curl -s -X POST "$GATEWAY/orders" \
  -H "Content-Type: application/json" \
  -d "{
        \"userId\": 999,
        \"items\": []
      }"
echo ""
echo ""

echo "============================================"
echo "🏁 TESTES FINALIZADOS DO ORDER-SERVICE"
echo "============================================"
