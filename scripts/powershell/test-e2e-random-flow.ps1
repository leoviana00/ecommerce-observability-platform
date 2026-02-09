# ============================================================
# DETECCAO DE AMBIENTE
# ============================================================

try {
    Resolve-DnsName ecommerce-gateway -ErrorAction Stop | Out-Null
    $INSIDE_DOCKER = $true
} catch {
    $INSIDE_DOCKER = $false
}

if ($INSIDE_DOCKER) {
    Write-Host "Ambiente detectado: DOCKER NETWORK"
    $HOST_GATEWAY = "ecommerce-gateway"
} else {
    Write-Host "Ambiente detectado: LOCALHOST"
    $HOST_GATEWAY = "localhost"
}

$PORT_GATEWAY = 8080

Write-Host "============================================"
Write-Host "E2E RANDOM FLOW TEST"
Write-Host "============================================"
Write-Host "Gateway: http://${HOST_GATEWAY}:${PORT_GATEWAY}"
Write-Host ""

# ============================================================
# Seleção aleatória de ID de usuário (equivalente ao Bash)
# ============================================================

# Gera um byte aleatório (0–255), equivalente a /dev/urandom
$RAND_BYTE = Get-Random -Minimum 0 -Maximum 256

if (($RAND_BYTE % 2) -eq 0) {
    $USER_ID = 999
} else {
    # Gera ID aleatório entre 1000 e 9999
    $USER_ID = Get-Random -Minimum 1000 -Maximum 10000
}

# Sufixo aleatório (0–9998)
$RANDOM_SUFFIX = Get-Random -Minimum 0 -Maximum 9999

Write-Host "USER_ID selecionado para o teste: $USER_ID"
Write-Host ""


# ============================================================
# HEALTH CHECK
# ============================================================

function Check-Service {
    param ($Url, $Name)
    try {
        Invoke-RestMethod -Uri $Url -Method GET | Out-Null
        Write-Host "OK - $Name"
    } catch {
        Write-Host "ERRO - $Name"
        exit 1
    }
}

Write-Host "Validando servicos..."
Check-Service "http://${HOST_GATEWAY}:${PORT_GATEWAY}/actuator/health" "gateway"

# ============================================================
# CRIAR PRODUTO (CORRIGIDO)
# ============================================================

Write-Host ""
Write-Host "Criando produto..."

$PRODUCT_BODY = @{
    name        = "Produto Teste $RANDOM_SUFFIX"
    description = "Simulacao stochastic"
    price       = 1
} | ConvertTo-Json

try {
    $PRODUCT_JSON = Invoke-RestMethod `
        -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/products" `
        -Method POST `
        -ContentType "application/json" `
        -Body $PRODUCT_BODY
} catch {
    Write-Host "Falha ao criar produto"
    Write-Host $_.Exception.Message
    exit 1
}

$PRODUCT_ID = $PRODUCT_JSON.id
Write-Host "Produto criado com ID: $PRODUCT_ID"

# ============================================================
# AJUSTAR ESTOQUE
# ============================================================

Write-Host ""
Write-Host "Ajustando estoque..."

$STOCK_BODY = @{
    amount = 5
    reason = "RESTOCK"
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/inventory/${PRODUCT_ID}/increase" `
    -Method POST `
    -ContentType "application/json" `
    -Body $STOCK_BODY | Out-Null

Write-Host "Estoque ajustado"

# ============================================================
# ADICIONAR AO CARRINHO
# ============================================================

Write-Host ""
Write-Host "Adicionando ao carrinho..."

$CART_BODY = @{
    productId = $PRODUCT_ID
    quantity  = 1
} | ConvertTo-Json

Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/cart/${USER_ID}/items" `
    -Method POST `
    -ContentType "application/json" `
    -Body $CART_BODY | Out-Null

# ============================================================
# CRIAR PEDIDO
# ============================================================

Write-Host ""
Write-Host "Criando pedido..."

$ORDER_BODY = @{
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
    -Body $ORDER_BODY

Write-Host "Pedido criado com ID: $($order.orderId)"

# ============================================================
# STATUS FINAL
# ============================================================

Write-Host ""
Write-Host "Aguardando processamento..."
Start-Sleep -Seconds 4

$status = Invoke-RestMethod `
    -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/orders/$($order.orderId)" `
    -Method GET

$status | ConvertTo-Json -Depth 5

Write-Host ""
Write-Host "============================================"
Write-Host "TESTE FINALIZADO COM SUCESSO"
Write-Host "============================================"
