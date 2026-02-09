# ============================================================
# CONFIGURACAO
# ============================================================

$HOST_GATEWAY = "localhost"
$PORT_GATEWAY = 8080   # Gateway ou API exposta
$RANDOM_SUFFIX = Get-Random -Maximum 99999

# ============================================================
# CRIAR PRODUTO
# ============================================================

Write-Host ""
Write-Host "Criando produto..."

$productBody = @{
    name        = "Produto Teste $RANDOM_SUFFIX"
    description = "Simulacao stochastic"
    price       = 1
} | ConvertTo-Json

try {
    $PRODUCT_RESPONSE = Invoke-RestMethod `
        -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/products" `
        -Method Post `
        -ContentType "application/json" `
        -Body $productBody
}
catch {
    Write-Host "ERRO ao criar produto:"
    Write-Host $_.Exception.Message
    exit 1
}

$PRODUCT_RESPONSE | ConvertTo-Json -Depth 10
$PRODUCT_ID = $PRODUCT_RESPONSE.id

if (-not $PRODUCT_ID) {
    Write-Host "ERRO: ID do produto nao retornado."
    exit 1
}

Write-Host "Produto criado com ID: $PRODUCT_ID"

# ============================================================
# AJUSTAR ESTOQUE (INCREASE)
# ============================================================

Write-Host ""
Write-Host "Ajustando estoque..."

$inventoryBody = @{
    amount = 5
    reason = "RESTOCK"
} | ConvertTo-Json

try {
    Invoke-RestMethod `
        -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/inventory/${PRODUCT_ID}/increase" `
        -Method Post `
        -ContentType "application/json" `
        -Body $inventoryBody
}
catch {
    Write-Host "ERRO ao ajustar estoque:"
    Write-Host $_.Exception.Message
    exit 1
}

Write-Host "Estoque ajustado com sucesso."

# ============================================================
# CONSULTAR ESTOQUE
# ============================================================

Write-Host ""
Write-Host "Consultando estoque..."

try {
    $INVENTORY_RESPONSE = Invoke-RestMethod `
        -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/inventory/${PRODUCT_ID}" `
        -Method Get
}
catch {
    Write-Host "ERRO ao consultar estoque:"
    Write-Host $_.Exception.Message
    exit 1
}

$INVENTORY_RESPONSE | ConvertTo-Json -Depth 10

Write-Host ""
Write-Host "Fluxo completo executado com sucesso."
