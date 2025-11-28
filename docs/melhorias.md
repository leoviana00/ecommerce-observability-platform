## ✅ Checklist Geral de Evolução – Plataforma eCommerce + Observability



## 1️⃣ Inventory Service – Arquitetura, Validação e Boas Práticas

- [x] Criar ProductNotFoundException

- [x] Implementar @RestControllerAdvice com JSON padronizado de erro

- [x] Substituir println por Logger padrão SLF4J

- [x] Tornar createInitialStock idempotente

- [x] Garantir atomicidade do decremento/incremento com @Transactional

- [ ] Criar base ApiException para padronizar resposta de erros

- [ ] Validar DTOs com @Valid, @NotNull, @Positive

- [ ] Criar enum padronizado InventoryStatus (AVAILABLE, UNAVAILABLE, LOW_STOCK)

## 2️⃣ Inventory Service – Testes Unitários e Integração

- [ ] Testar createInitialStock para novo produto

- [ ] Testar createInitialStock para produto já existente

- [ ] Testar GET /inventory/{productId} (200/404)

- [ ] Testar producer de inventory-updated

- [ ] Testar consumer product-created

- [ ] Testar comportamento idempotente em reprocessamento de eventos Kafka

- [ ] Testar concorrência no método de decremento (cenários com lock)

## 3️⃣ Inventory Service – Observabilidade

- [x] Adicionar Spring Boot Actuator

- [x] Expor /actuator/health, /actuator/metrics

- [ ] Criar métricas customizadas (eventos processados, falhas, latência)

- [ ] Adicionar KafkaHealthIndicator

- [ ] Criar logs estruturados (key=value)

- [ ] Adicionar tracing distribuído (Tempo/OpenTelemetry)

## 4️⃣ Product Service – Integração e Eventos Kafka

- [x] Criar evento Kafka product-created

- [x] Garantir envio do evento na criação do produto

- [ ] Documentar contrato do evento

- [ ] Criar mecanismo de versionamento de eventos (para evolução futura)

- [ ] Adicionar testes unitários do producer

- [ ] Adicionar testes integração Kafka-embedded

## 5️⃣ Integração Inventory ↔ Product ↔ Order ↔ Cart

- [x] Inventory consome product-created

- [x] Cart-service consulta inventory via REST

- [x] Order-service cria pedido com itens do cart

- [ ] Inventory-service reagir a order-paid para decrementar estoque (futuro)

- [ ] Criar evento inventory-reserved e inventory-rejected (SAGA completa)

- [ ] Criar compensação automática para falhas entre serviços

- [ ] Implementar mecanismo de idempotência no cart-service e order-service

## 6️⃣ Order Service – Evolução e Boas Práticas

- [x] Criar fluxo completo do pedido (REST → persistência)

- [x] Criar evento order-created (gatilho da SAGA)

- [x] Consumir payment-processed e payment-failed

- [x] Publicar evento order-paid

- [x] Validar itens antes de criar pedido

- [ ] Criar camada de service para buscar preço real do product-service

- [ ] Implementar compensação no order-service (rollback de pedido)

- [ ] Criar testes unitários + integração (RestAssured)

- [ ] Garantir idempotência de order-created e order-paid

7️⃣ Payment Service – Evolução

- [x] Criar estrutura base do microserviço

- [x] Consumir order-created

- [x] Publicar payment-processed ou payment-failed

- [x] Simulação de aprovação/recusa

- [ ] Criar teste Kafka de consumo

- [ ] Criar simulação configurável (mock, random, retry, SLA)

- [ ] Implementar regras reais de antifraude (futuro)

- [ ] Expor endpoint admin para forçar pagamento falhar/sucesso (debug)

## 8️⃣ Cart Service – Evolução

- [x] Criar carrinho com validação de estoque

- [x] Integrar com inventory-service

- [x] Criar remoção, adição e consulta via REST

- [ ] Integrar com product-service para obter preço real

- [ ] Implementar expiração automática de carrinho (TTL)

- [ ] Criar testes unitários e integração

## 9️⃣ Kafka – Padronização e Resiliência

- [x] Criar padronização centralizada (SkyFolder Kafka)

- [ ] Criar interceptors de logging Kafka (producer/consumer)

- [ ] Implementar DLT (Dead Letter Topic) para eventos inválidos

- [ ] Implementar retry com backoff exponencial

- [ ] Documentar versionamento de eventos Kafka

- [ ] Criar contrato JSON Schema dos eventos

## 🔟 Observabilidade Completa – Plataforma (Infra + Serviços)

- [x] Integrar Actuator nos serviços

- [ ] Integrar Prometheus + Grafana

- [ ] Criar dashboard SAGA (pedido → pagamento → estoque)

- [ ] Integrar Loki para logs estruturados

- [ ] Integrar Tempo/Jaeger para tracing distribuído

- [ ] Criar painéis de erros Kafka (DLQ, retries, offset)

- [ ] Criar ranking de latência por microserviço

## 1️⃣1️⃣ DevOps / Infra – Automação e Robustez

- [ ] Criar docker-compose com todos os serviços

- [ ] Criar ambiente Kubernetes (futuro)

- [ ] Criar serviços de readiness/liveness probes

- [ ] Criar pipeline CI/CD (lint, test, build)

- [ ] Criar versionamento semântico (semver) automático

## 1️⃣2️⃣ Documentação e Diagramação

- [x] Criar SkyFolder geral da plataforma

- [x] Criar documentação do fluxo Order

- [x] Criar documentação Kafka Producers/Consumers

- [ ] Criar diagrama SAGA completo (pedido, pagamento, estoque)

- [ ] Criar diagramas C4 nível 1, 2 e 3

- [ ] Criar documentação OpenAPI automaticamente