#!/bin/bash
GATEWAY="http://localhost:8080"
USER_ID=123

echo "1) create product"
P=$(curl -s -X POST "$GATEWAY/products" -H "Content-Type: application/json" -d '{"name":"X","description":"t","price":10.0,"stock":1}')
PID=$(echo "$P" | jq -r '.id')
echo "productId=$PID"

echo "2) restock"
curl -s -X POST "$GATEWAY/inventory/$PID/increase" -H "Content-Type: application/json" -d '{"amount":10,"reason":"test"}' >/dev/null

echo "3) add to cart"
curl -s -X POST "$GATEWAY/cart/$USER_ID/items" -H "Content-Type: application/json" -d "{\"productId\": $PID, \"quantity\": 1}" >/dev/null

echo "4) create order"
ORD=$(curl -s -X POST "$GATEWAY/orders" -H "Content-Type: application/json" -d "{\"userId\": $USER_ID, \"items\":[{\"productId\":$PID,\"quantity\":1}]}")
echo "order response: $ORD"
ORDER_ID=$(echo "$ORD" | jq -r '.orderId')
echo "orderId=$ORDER_ID"

echo "5) wait for payment processing (sleep 3s)"
sleep 3

echo "6) get order status"
curl -s -X GET "$GATEWAY/orders/$ORDER_ID" | jq
