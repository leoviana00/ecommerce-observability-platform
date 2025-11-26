## 🚀 Atividades 

## 📦 Desenvolvimento dos serviços - Stack Ecommerce

- [ ] [ecommerce-gateway: API Gateway, ponto de entrada do cliente.](./ecommerce-stack/ecommerce-gateway.md)
- [ ] [product-service: base de tudo, todos os outros serviços consultam produtos](./ecommerce-stack/product-service.md)
- [ ] [inventory-service: depende do product; usado por cart, order, payment](./ecommerce-stack/inventory-service.md)
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


## 🗺️  Roadmap  

Priorizando a base e o fluxo principal:

## 📐 Fase 1: Base e Design (Sprints 1-3)

[x] **ecommerce-architecture** (Design, Diagramas, Padrões de Microserviços)

[ ] **ecommerce-infra** (Cluster, Rede, Banco de Dados, CI/CD Básico)

[x] **ecommerce-gateway** (Gateway API inicial)

[x] **product-service** (Serviço de Produto - Base do catálogo)

## 🛒 Fase 2: Fluxo de Compra Principal (Sprints 4-8)

[ ] **inventory-service** (Gestão de Estoque, depende de Product)

[ ] **cart-service** (Carrinho de Compras, depende de Product e Inventory)

[ ] **order-service** (Criação e Gestão de Pedidos, depende de Cart, Product e Inventory)

[ ] **payment-service** (Integração de Pagamento, depende de Order)

[ ] **Teste E2E do Fluxo de Compra**

## 📈 Fase 3: Observabilidade e Complementos (Sprints 9-12)

[ ] **ecommerce-observability-stack** (Logging, Metrics, Tracing)

[ ] **notification-service** (Envio de eventos via e-mail/SMS, depende de Order/Payment)

[ ] **Stack Monitoring** (monitor-health-service, monitor-lag-service, monitor-alert-dispatcher)

[ ] Refinamento da Arquitetura e Otimização de Performance