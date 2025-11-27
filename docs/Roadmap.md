## 🚀 Atividades 

## 📦 Desenvolvimento dos serviços - Stack Ecommerce

- [x] [ecommerce-gateway: API Gateway, ponto de entrada do cliente.](./ecommerce-stack/ecommerce-gateway.md)
- [x] [product-service: base de tudo, todos os outros serviços consultam produtos](./ecommerce-stack/product-service.md)
- [x] [inventory-service: depende do product; usado por cart, order, payment](./ecommerce-stack/inventory-service.md)
- [ ] [cart-service: depende de product + inventory](./ecommerce-stack/cart-service.md)
- [ ] [order-service: depende de cart + product + inventory](./ecommerce-stack/order-service.md)
- [ ] [payment-service: depende de order](./ecommerce-stack/payment-service.md)
- [ ] [notification-service: depende de order/payment (eventos)](./ecommerce-stack/notification-service.md)

## Orquestração do fluxo

- Uma vez que cada serviço básico esteja rodando:

- [ ] Testar o fluxo completo do cliente até o pedido:
  - Cliente visualiza produtos (product-service).
  - Adiciona ao carrinho (cart-service).
  - Cria pedido (order-service), decrementa estoque (inventory-service).
  - Processa pagamento (payment-service).
  - Notifica o cliente (notification-service).


## 📊 Desenvolvimento dos serviços - Stack Monitoring

- [ ] [monitor-health-service](./monitoring-stack/monitor-health-service.md)
- [ ] [monitor-lag-service](./monitoring-stack/monitor-lag-service.md)
- [ ] [monitor-consumer-presence-service](./monitoring-stack/monitor-consumer-presence-service.md)
- [ ] [monitor-state-manager](./monitoring-stack/monitor-state-manager.md)
- [ ] [monitor-alert-dispatcher](./monitoring-stack/monitor-alert-dispatcher.md)


## 🏗️ Preparando o ambiente - Stack Infraestrutura

- [ ] [ecommerce-infra](./infra-stack/ecommerce-infra.md)
- [ ] [ecommerce-observability-stack](./infra-stack/ecommerce-observability-stack.md)
- [ ] [ecommerce-architecture](./infra-stack/ecommerce-architecture.md)
