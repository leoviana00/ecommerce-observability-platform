# ============================================================
# CONFIGURACAO
# ============================================================

$HOST_GATEWAY = "localhost"
$PORT_GATEWAY = 8080
$USER_ID = 1

Write-Host "============================================"
Write-Host "INICIANDO TESTE E2E - ECOMMERCE PLATFORM"
Write-Host "============================================"

# ============================================================
# CRIAR PRODUTO
# ============================================================

Write-Host "`nCriando produto..."

$randomSuffix = Get-Random -Minimum 1000 -Maximum 99999

$productBody = @{
    name        = "Produto Teste $randomSuffix"
    description = "Simulacao stochastic"
    price       = 1
} | ConvertTo-Json

$product = Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/products" `
    -Method POST `
    -ContentType "application/json" `
    -Body $productBody

$product | ConvertTo-Json -Depth 5

$PRODUCT_ID = $product.id

if (-not $PRODUCT_ID) {
    Write-Host "Falha ao criar produto"
    exit 1
}

Write-Host "Produto criado com ID: $PRODUCT_ID"

# ============================================================
# AJUSTAR ESTOQUE (RESTOCK)
# ============================================================

Write-Host "`nAjustando estoque..."

$inventoryBody = @{
    amount = 5
    reason = "RESTOCK"
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/inventory/${PRODUCT_ID}/increase" `
    -Method POST `
    -ContentType "application/json" `
    -Body $inventoryBody

Write-Host "Estoque ajustado com sucesso"

# ============================================================
# CONSULTAR ESTOQUE
# ============================================================

Write-Host "`nConsultando estoque..."

$inventory = Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/inventory/${PRODUCT_ID}" `
    -Method GET

$inventory | ConvertTo-Json -Depth 5

# ============================================================
# ADICIONAR ITEM AO CARRINHO
# ============================================================

Write-Host "`nAdicionando item ao carrinho..."

$cartItemBody = @{
    productId = $PRODUCT_ID
    quantity  = 1
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/cart/${USER_ID}/items" `
    -Method POST `
    -ContentType "application/json" `
    -Body $cartItemBody

Write-Host "Item adicionado ao carrinho"

# ============================================================
# CONSULTAR CARRINHO
# ============================================================

Write-Host "`nConsultando carrinho..."

$cart = Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/cart/${USER_ID}" `
    -Method GET

$cart | ConvertTo-Json -Depth 5

if (-not $cart.items -or $cart.items.Count -eq 0) {
    Write-Host "Carrinho esta vazio apos inclusao"
    exit 1
}

Write-Host "Carrinho validado com sucesso"

# ============================================================
# CRIAR PEDIDO
# ============================================================

Write-Host "`nCriando pedido..."

$orderBody = @{
    userId = $USER_ID
    items  = @(
        @{
            productId = $PRODUCT_ID
            quantity  = 1
        }
    )
} | ConvertTo-Json -Depth 5

$order = Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/orders" `
    -Method POST `
    -ContentType "application/json" `
    -Body $orderBody

$order | ConvertTo-Json -Depth 5

$ORDER_ID = $order.orderId

if (-not $ORDER_ID) {
    Write-Host "Falha ao criar pedido"
    exit 1
}

Write-Host "Pedido criado com ID: $ORDER_ID"

# ============================================================
# AGUARDAR PROCESSAMENTO
# ============================================================

Write-Host "`nAguardando processamento do pedido..."
Start-Sleep -Seconds 4

# ============================================================
# CONSULTAR STATUS FINAL DO PEDIDO
# ============================================================

Write-Host "`nConsultando status final do pedido..."

$orderStatus = Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/orders/${ORDER_ID}" `
    -Method GET

$orderStatus | ConvertTo-Json -Depth 5

Write-Host "`n============================================"
Write-Host "TESTE FINALIZADO COM SUCESSO"
Write-Host "============================================"
