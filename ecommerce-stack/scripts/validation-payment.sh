#!/bin/bash

GATEWAY="http://localhost:8080"
USER_ID=123

echo "============================================"
echo "💳 TESTES DO PAYMENT-SERVICE"
echo "============================================"

echo "🔵 1. Criando produto para testes..."
CREATE_PRODUCT_RESPONSE=$(curl -s -X POST "$GATEWAY/products" \
  -H "Content-Type: application/json" \
  -d '{
        "name":"Produto para Pagamento",
        "description":"Gerado automaticamente",
        "price":100.0,
        "stock":5
      }')

echo "$CREATE_PRODUCT_RESPONSE"

PRODUCT_ID=$(echo "$CREATE_PRODUCT_RESPONSE" | grep -o '"id"[ ]*:[ ]*[0-9]*' | cut -d ':' -f2 | tr -d ' ')
echo "➡️  Produto criado com ID: $PRODUCT_ID"
echo ""

echo "🔧 2. Ajustando estoque inicial..."
curl -s -X POST "$GATEWAY/inventory/$PRODUCT_ID/increase" \
  -H "Content-Type: application/json" \
  -d '{"amount":10, "reason":"RESTOCK"}' >/dev/null

echo "✔ Estoque ajustado!"
echo ""

echo "🔵 3. Consultando estoque atualizado..."
curl -s -X GET "$GATEWAY/inventory/$PRODUCT_ID"
echo ""
echo ""

echo "🛒 4. Criando carrinho com item..."
ADD_CART=$(curl -s -X POST "$GATEWAY/cart/$USER_ID/items" \
  -H "Content-Type: application/json" \
  -d "{
        \"productId\": $PRODUCT_ID,
        \"quantity\": 2
      }")

echo "$ADD_CART"
echo "✔ Item adicionado ao carrinho!"
echo ""

echo "🛒 5. Consultando carrinho..."
CART_RESPONSE=$(curl -s -X GET "$GATEWAY/cart/$USER_ID")
echo "$CART_RESPONSE"
echo ""
echo ""

echo "🧾 6. Criando pedido..."
ORDER_RESPONSE=$(curl -s -X POST "$GATEWAY/orders" \
  -H "Content-Type: application/json" \
  -d "{
        \"userId\": $USER_ID,
        \"items\": [{
            \"productId\": $PRODUCT_ID,
            \"quantity\": 2
        }]
      }")

echo "$ORDER_RESPONSE"
ORDER_ID=$(echo "$ORDER_RESPONSE" | grep -o '"orderId"[ ]*:[ ]*[0-9]*' | cut -d ':' -f2 | tr -d ' ')
echo "➡️  Pedido criado com ID: $ORDER_ID"
echo ""

echo "⏳ 7. Aguardando processamento do pagamento..."
sleep 3

echo "📡 8. Resultado do pagamento (via consumo de logs):"
echo "⚠️  OBS: O payment-service não retorna via REST, somente por evento Kafka."
echo "        Verifique os logs do container/terminal do payment-service:"
echo ""
echo "    • payment-processed (APROVADO)"
echo "    • payment-failed (RECUSADO)"
echo ""
echo "Exemplo de log esperado:"
echo "📤 Enviado evento payment-processed: {...}"
echo ""
echo ""

echo "🔴 9. Testando pedido inexistente..."
curl -s -X GET "$GATEWAY/orders/999999"
echo ""
echo ""

echo "============================================"
echo "🏁 TESTES FINALIZADOS DO PAYMENT-SERVICE"
echo "============================================"
