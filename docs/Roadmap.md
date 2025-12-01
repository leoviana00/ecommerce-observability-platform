# 🚀 Roadmap 

# 📦 Desenvolvimento dos Serviços – Stack Ecommerce

| Serviço              | Status      | Descrição                                              |
| -------------------- | ----------- | ------------------------------------------------------ |
| ecommerce-gateway    | ✔ Concluído | API Gateway / entrypoint                               |
| product-service      | ✔ Concluído | CRUD + Kafka: product-created                          |
| inventory-service    | ✔ Concluído | Estoque + reservas + eventos                           |
| cart-service         | ✔ Concluído | Carrinho integrado ao inventory                        |
| order-service        | ✔ Concluído | Criação de pedido + SAGA: order-created/order-paid     |
| payment-service      | ✔ Concluído | Consumidor de order-created + payment-processed/failed |
| notification-service | ⏳ A Fazer   | Consumir order-paid e enviar notificações              |

## 🔁 Orquestração do Fluxo (SAGA Ecommerce)

- [x] Criar produto

- [x] Inventário inicial via Kafka

- [x] Adicionar itens ao carrinho

- [x] Criar pedido (publica order-created)

- [x] Processar pagamento (payment-service)

- [x] Atualizar pedido (order-service)

- [x] Aplicar SAGA completa integrando inventory → order-paid

- [ ] Integrar notification-service (evento order-paid)

# 📊 Desenvolvimento dos Serviços – Stack Monitoring

| Serviço                           | Status    |
| --------------------------------- | --------- |
| monitor-health-service            | ⏳ A Fazer |
| monitor-lag-service               | ⏳ A Fazer |
| monitor-consumer-presence-service | ⏳ A Fazer |
| monitor-state-manager             | ⏳ A Fazer |
| monitor-alert-dispatcher          | ⏳ A Fazer |

## 1. Fundamentos da Mesh

- [ ] Criar contratos JSON

- [ ] Criar monitor-state-manager

- [ ] Criar armazenamento Redis

- [ ] Criar mecanismo de deduplicação

- [ ] Criar monitor-alert-dispatcher básico (Telegram)

## 2. Monitores Ativos

- [ ] Criar monitor-health-service

- [ ] Criar monitor-lag-service

- [ ] Criar monitor-consumer-presence-service

- [ ] Conectar todos ao state-manager

- [ ] Configurar thresholds e severidades

## 3. Integração com eCommerce

- [ ] Adicionar Actuator para todos os serviços do eCommerce

- [ ] Criar dashboard Grafana com health metrics

- [ ] Criar alertas avançados por tópico

## 4. Hardening & Experimentos

- [ ] Testcontainers para simular falhas

- [ ] Chaos engineering leve

- [ ] Regras avançadas de deduplicação

- [ ] Métricas Kafka específicas (lag velocity)

- [ ] Documentação completa


## 🏗️ Preparação do Ambiente – Stack Infraestrutura

| Componente                                                       | Status          |
| ---------------------------------------------------------------- | --------------- |
| ecommerce-infra (Docker, Kafka, Zookeeper, DB)                   | ⏳ Em Construção|
| ecommerce-observability-stack (Prometheus, Grafana, Loki, Tempo) | ⏳ A Fazer      |
| ecommerce-architecture (Diagramas + doc infra)                   | ✔ Concluído     |

## 🔧 Melhorias Técnicas (Checklist rápido)

- [x] Erros padronizados com @RestControllerAdvice

- [x] Eventos Kafka padronizados com DTOs dedicados

- [x] Order-service integrado ao payment-service

- [ ] Implementar idempotência completa (order/payment/inventory)

- [ ] Criar DLQs e retries customizados Kafka

- [ ] Integrar preço real via product-service

- [ ] Criar métricas customizadas (Prometheus)

- [ ] Gerar documentação OpenAPI (Swagger)