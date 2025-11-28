#!/bin/bash

GATEWAY_URL="http://localhost:8080"

echo "============================================"
echo "🔵 1. Criando produto"
echo "============================================"

CREATE_RESPONSE=$(curl -s -X POST "$GATEWAY_URL/products" \
  -H "Content-Type: application/json" \
  -d '{
        "name":"Notebook Gamer",
        "description":"Notebook potente",
        "price":7500.0,
        "stock":10
      }')

echo "$CREATE_RESPONSE"
echo ""

# Extrair o ID do JSON retornado
PRODUCT_ID=$(echo "$CREATE_RESPONSE" | grep -o '"id"[[:space:]]*:[[:space:]]*[0-9]*' | grep -o '[0-9]*')

if [ -z "$PRODUCT_ID" ]; then
  echo "❌ ERRO: Não foi possível capturar o ID do produto criado."
  exit 1
fi

echo "➡️  ID capturado para testes: $PRODUCT_ID"
echo ""


echo "============================================"
echo "🔵 2. Consultando estoque inicial (productId=$PRODUCT_ID)"
echo "============================================"

curl -X GET "$GATEWAY_URL/inventory/$PRODUCT_ID"
echo -e "\n\n"


echo "============================================"
echo "🟢 3. Aumentando estoque (amount=5, reason=RESTOCK)"
echo "============================================"

curl -X POST "$GATEWAY_URL/inventory/$PRODUCT_ID/increase" \
  -H "Content-Type: application/json" \
  -d '{
        "amount": 5,
        "reason": "RESTOCK"
      }'

echo -e "\n\n"


echo "============================================"
echo "🟢 4. Consultando estoque após increase"
echo "============================================"

curl -X GET "$GATEWAY_URL/inventory/$PRODUCT_ID"
echo -e "\n\n"


echo "============================================"
echo "🟠 5. Reduzindo estoque (amount=3, reason=ORDER)"
echo "============================================"

curl -X POST "$GATEWAY_URL/inventory/$PRODUCT_ID/decrease" \
  -H "Content-Type: application/json" \
  -d '{
        "amount": 3,
        "reason": "ORDER"
      }'

echo -e "\n\n"


echo "============================================"
echo "🔴 6. Reduzindo estoque com valor maior que o disponível (erro esperado)"
echo "============================================"

curl -X POST "$GATEWAY_URL/inventory/$PRODUCT_ID/decrease" \
  -H "Content-Type: application/json" \
  -d '{
        "amount": 999,
        "reason": "ORDER"
      }'

echo -e "\n\n"


echo "============================================"
echo "🔴 7. Testando aumento inválido (amount = 0)"
echo "============================================"

curl -X POST "$GATEWAY_URL/inventory/$PRODUCT_ID/increase" \
  -H "Content-Type: application/json" \
  -d '{
        "amount": 0,
        "reason": "INVALID"
      }'

echo -e "\n\n"


echo "============================================"
echo "🔴 8. Testando produto inexistente (id=99999)"
echo "============================================"

curl -X GET "$GATEWAY_URL/inventory/99999"
echo -e "\n\n"


echo "============================================"
echo "🏁 FINALIZADO"
echo "============================================"
