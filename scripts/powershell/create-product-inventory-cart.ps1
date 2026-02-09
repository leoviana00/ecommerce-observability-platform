# ============================================================
# CONFIGURACOES
# ============================================================
$HOST_GATEWAY = 'localhost'
$PORT_GATEWAY = 8080
$USER_ID = 1
$RANDOM_SUFFIX = Get-Random

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

# ============================================================
# CRIAR PRODUTO
# ============================================================
Write-Host "`nCriando produto..."

$productBody = @{
    name        = "Produto Teste $RANDOM_SUFFIX"
    description = 'Simulacao stochastic'
    price       = 1
} | ConvertTo-Json

$productResponse = Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/products" `
    -Method POST `
    -ContentType 'application/json' `
    -Body $productBody

$productResponse | ConvertTo-Json -Depth 5
$PRODUCT_ID = $productResponse.id

if (-not $PRODUCT_ID) {
    Write-Host 'Falha ao criar produto'
    exit 1
}

Write-Host "Produto criado com ID: $PRODUCT_ID"

# ============================================================
# AJUSTAR ESTOQUE
# ============================================================
Write-Host "`nAjustando estoque..."

$inventoryBody = @{
    amount = 5
    reason = 'RESTOCK'
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/inventory/${PRODUCT_ID}/increase" `
    -Method POST `
    -ContentType 'application/json' `
    -Body $inventoryBody

Write-Host 'Estoque ajustado com sucesso'

# ============================================================
# CONSULTAR ESTOQUE
# ============================================================
Write-Host "`nConsultando estoque..."

$inventory = Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/inventory/${PRODUCT_ID}" `
    -Method GET

$inventory | ConvertTo-Json -Depth 5

# ============================================================
# ADICIONAR AO CARRINHO
# ============================================================
Write-Host "`nAdicionando item ao carrinho..."

$cartBody = @{
    productId = $PRODUCT_ID
    quantity  = 1
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/cart/${USER_ID}/items" `
    -Method POST `
    -ContentType 'application/json' `
    -Body $cartBody

Write-Host 'Item adicionado ao carrinho'

# ============================================================
# CONSULTAR CARRINHO
# ============================================================
Write-Host "`nConsultando carrinho..."

$cart = Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/cart/${USER_ID}" `
    -Method GET

$cart | ConvertTo-Json -Depth 5

# ============================================================
# CHECK FINAL
# ============================================================
if ($cart.items -and $cart.items.Count -gt 0) {
    Write-Host "`nTESTE FINALIZADO COM SUCESSO"
} else {
    Write-Host "`nCarrinho esta vazio apos inclusao"
    exit 1
}
