## 🔹 Fluxo do Cliente - Pedido

## 1️⃣ Cliente cria produto

  - Serviço: product-service
  - Ação: POST /products
  - Evento Kafka disparado: product-created
  - Consumidor: inventory-service (cria estoque inicial)

## 2️⃣ Estoque inicial é criado

  - Serviço: inventory-service
  - Ação: cria registro no banco, stockQuantity = 0 ou inicial
  - Evento Kafka opcional: inventory-updated (quando estoque muda)

## 3️⃣ Cliente adiciona produto ao carrinho

  - Serviço: cart-service
  - Ação: POST /cart ou PATCH /cart/add
  - Validação: opcionalmente verificar estoque disponível via inventory-service

## 4️⃣ Cliente finaliza pedido

  - Serviço: order-service
  - Ação: POST /orders
  - Verifica:
    - Estoque disponível no inventory-service
    - Atualiza estoque (movimentação: decrementa)
  - Evento Kafka disparado: order-created

## 5️⃣ Pagamento

  - Serviço: payment-service
  - Ação: processar pagamento via POST /payments
  - Evento Kafka: payment-processed ou payment-failed

## 6️⃣ Notificações
  
  - Serviço: notification-service
  - Consome eventos:
    - order-created: notificar cliente + vendedor
    - payment-processed: notificar sucesso
    - payment-failed: notificar falha