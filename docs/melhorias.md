## ✅ Checklist de Próximos Passos – Inventory & Product Services

## 1️⃣ Inventory Service – Refatoração e Boas Práticas
- [x] Criar `ProductNotFoundException` (substituir `RuntimeException`)
- [ ] Criar base `ApiException` opcional para padronizar erros
- [x] Usar `@RestControllerAdvice` para retornar JSON padronizado
- [ ] Validar DTOs com `@Valid`, `@NotNull`, `@Positive`
- [ ] Substituir `System.out.println` por `Logger`

## 2️⃣ Inventory Service – Testes Unitários e Integração
- [ ] Testar `createInitialStock` (novo produto e produto existente)
- [ ] Testar `getInventoryResponse` (produto existente e não existente)
- [ ] Testar controller GET `/inventory/{productId}` (validar 200 e 404)
- [ ] Testar Kafka consumer (`ProductCreatedEvent`)
- [ ] Testar cenário de offset reprocessado (evitar duplicação de estoque)

## 3️⃣ Observabilidade – Inventory Service
- [ ] Adicionar Spring Boot Actuator (`spring-boot-starter-actuator`)
- [ ] Expor endpoints `/actuator/health` e `/actuator/metrics`
- [ ] Criar métricas customizadas (ex: eventos processados)
- [ ] Configurar `KafkaHealthIndicator` para monitorar consumers

## 4️⃣ Integração entre Serviços
- [ ] Garantir Product Service publica evento Kafka ao criar produto
- [x] Garantir Inventory Service consome evento e cria estoque inicial
- [ ] Validar atualização futura do estoque via eventos
- [ ] Configurar DLQ e retries Kafka para eventos falhos

## 5️⃣ Melhorias e Refinamentos
- [ ] Padronizar respostas e erros JSON (timestamp, status, message)
- [ ] Mapear DTO ↔ Entity com MapStruct (reduz boilerplate)
- [ ] Configurar retries e backoff Kafka (Inventory/Product Service)
- [ ] Monitorar Dead Letter Queue (evita perda de eventos)
- [ ] Documentar endpoints e fluxo Kafka (README, diagramas)
- [ ] Criar diagrama fluxo Product → Kafka → Inventory → DB
