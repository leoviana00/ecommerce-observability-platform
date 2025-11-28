#!/bin/bash

GATEWAY_URL="http://localhost:8080"

# cores
GREEN="\e[32m"
RED="\e[31m"
BLUE="\e[34m"
YELLOW="\e[33m"
RESET="\e[0m"

echo -e "${BLUE}============================================"
echo -e "🛒 TESTES DO CART-SERVICE"
echo -e "============================================${RESET}"


# ======================================================
# 1. Criar produto
# ======================================================
echo -e "${BLUE}🔵 1. Criando produto para testes...${RESET}"

CREATE_PRODUCT=$(curl -s -X POST "$GATEWAY_URL/products" \
  -H "Content-Type: application/json" \
  -d '{
        "name":"Mouse Gamer",
        "description":"Mouse RGB",
        "price":250.0,
        "stock":10
      }')

echo "$CREATE_PRODUCT"
PRODUCT_ID=$(echo "$CREATE_PRODUCT" | jq -r '.id')

echo -e "➡️  Produto criado com ID: ${GREEN}$PRODUCT_ID${RESET}\n"


# ======================================================
# 2. Forçar criação de estoque (caso evento não tenha chego)
# ======================================================
echo -e "${BLUE}🔧 2. Ajustando estoque inicial (increase)...${RESET}"

curl -s -X POST "$GATEWAY_URL/inventory/$PRODUCT_ID/increase" \
  -H "Content-Type: application/json" \
  -d '{"amount": 10, "reason": "RESTOCK_FOR_TEST"}'

echo -e "\n"


# ======================================================
# 3. Consultar estoque
# ======================================================
echo -e "${BLUE}🔵 3. Consultando estoque atualizado...${RESET}"

curl -s -X GET "$GATEWAY_URL/inventory/$PRODUCT_ID"
echo -e "\n"


# ======================================================
# 4. Definir userId
# ======================================================
USER_ID=123
echo -e "🧑 Usuário de teste: ${GREEN}$USER_ID${RESET}\n"


# ======================================================
# 5. Adicionar item ao carrinho
# ======================================================
echo -e "${GREEN}🟢 4. Adicionando item ao carrinho...${RESET}"

ADD_RESPONSE=$(curl -s -X POST "$GATEWAY_URL/cart/$USER_ID/items" \
  -H "Content-Type: application/json" \
  -d "{
        \"productId\": $PRODUCT_ID,
        \"quantity\": 2
      }")

echo "$ADD_RESPONSE"
echo -e "\n"


# ======================================================
# 6. Consultar carrinho
# ======================================================
echo -e "${GREEN}🟢 5. Consultando carrinho...${RESET}"

curl -s -X GET "$GATEWAY_URL/cart/$USER_ID"
echo -e "\n"


# ======================================================
# 7. Remover item
# ======================================================
echo -e "${YELLOW}🟠 6. Removendo item do carrinho...${RESET}"

curl -s -X DELETE "$GATEWAY_URL/cart/$USER_ID/items/$PRODUCT_ID"
echo -e "\n"


# ======================================================
# 8. Consultar carrinho pós remoção
# ======================================================
echo -e "${YELLOW}🟠 7. Consultando carrinho após remoção...${RESET}"

curl -s -X GET "$GATEWAY_URL/cart/$USER_ID"
echo -e "\n"


# ======================================================
# 9. Teste de erro
# ======================================================
echo -e "${RED}🔴 8. Testando erro com produto inexistente...${RESET}"

curl -s -X POST "$GATEWAY_URL/cart/$USER_ID/items" \
  -H "Content-Type: application/json" \
  -d '{
        "productId": 99999,
        "quantity": 1
      }'

echo -e "\n"


# ======================================================
# FINAL
# ======================================================
echo -e "${BLUE}============================================"
echo -e "🏁 TESTES FINALIZADOS DO CART-SERVICE"
echo -e "============================================${RESET}\n"
