# ============================
# Configurações
# ============================
$HOST_GATEWAY = "localhost"
$PORT_GATEWAY = 8080

# Sufixo aleatório
$RANDOM_SUFFIX = Get-Random

# Payload do produto
$body = @{
    name        = "Produto Teste $RANDOM_SUFFIX"
    description = "Simulacao: Cadastro de Produto"
    price       = 1
} | ConvertTo-Json

# ============================
# Requisição POST
# ============================
try {
    $PRODUCT_JSON = Invoke-RestMethod `
        -Uri "http://${HOST_GATEWAY}:${PORT_GATEWAY}/products" `
        -Method Post `
        -ContentType "application/json" `
        -Body $body
}
catch {
    Write-Host "Falha ao criar produto." -ForegroundColor Red
    Write-Host $_.Exception.Message
    exit 1
}

# ============================
# Exibir resposta
# ============================
$PRODUCT_JSON | ConvertTo-Json -Depth 10

# ============================
# Validar ID
# ============================
$PRODUCT_ID = $PRODUCT_JSON.id

if (-not $PRODUCT_ID) {
    Write-Host "Falha ao criar produto. ID inválido." -ForegroundColor Red
    exit 1
}
else {
    Write-Host "Produto criado com ID: $PRODUCT_ID" -ForegroundColor Green
}
