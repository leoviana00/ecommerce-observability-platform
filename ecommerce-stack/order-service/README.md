## 📦 order-service
Microserviço gerado automaticamente pelo script generate-service.sh.Este projeto segue o padrão arquitetural SkyFolder utilizado por toda a plataforma.

## 1. 🎯 Objetivo do Serviço
O propósito principal do order-service é gerenciar o ciclo de vida completo de um pedido (criação, consulta, atualização de status). Ele atua como o coordenador central (SAGA Orchestrator) das transações distribuídas, garantindo que a reserva de estoque e o pagamento ocorram de forma coesa.

## 2. 🧠 Responsabilidades do Serviço

- Criar e persistir pedidos (OrderEntity) e seus itens (OrderItemEntity).
- Coordenar a transação SAGA para reserva de estoque e pagamento, emitindo OrderCreatedEvent.
- Consumir eventos de pagamento (payment-processed, payment-failed) para atualizar o status do pedido (PAID, PAYMENT_FAILED).
- Expor endpoints REST para clientes e outros serviços criarem e consultarem o status de pedidos.

## ❌ O que este serviço não faz (para evitar confusão):

- Não controla o estoque (função do inventory-service).
- Não processa pagamentos (função do payment-service).
- Não mantém dados de catálogo (nome, descrição); apenas armazena o preço de snapshot (unitPrice) no momento da compra.

## 3. 🏗️ Arquitetura Interna (SkyFolder)

Estrutura padronizada do microserviço:

```bash
src/main/java/io/viana/order_service/
  ├── controller/       // APIs REST (criação e consulta: OrderController)
  ├── service/          // Lógica de negócio e orquestração SAGA (OrderService)
  ├── repository/       // Spring Data JPA (OrderRepository, OrderItemRepository)
  ├── model/            // Entidades JPA (OrderEntity, OrderItemEntity)
  ├── dto/              // DTOs de Request/Response
  │    └── events/      // DTOs de Eventos Kafka (OrderCreatedEvent, PaymentProcessedEvent, etc.)
  ├── producer/         // Componente para envio de eventos (OrderEventProducer)
  ├── consumer/         // Componente para consumo de eventos (OrderPaymentConsumer)
  ├── config/           // Configuração de Beans (KafkaConfig)
  └── exception/        // Exceções customizadas (OrderNotFoundException)
src/main/resources/
```

## 4. 📡 APIs Principais

**Endpoints do Order Service**

| Método | Endpoint            | Descrição                                                                                             | Corpo / Retorno           |
|--------|----------------------|---------------------------------------------------------------------------------------------------------|----------------------------|
| **POST** | `/orders`            | Cria um novo pedido no status **CREATED** e inicia a transação SAGA publicando `order-created` no Kafka. | **Request:** CreateOrderRequest<br>**Response:** OrderResponse |
| **GET**  | `/orders/{orderId}`  | Retorna os detalhes completos e o status atual do pedido. Retorna **404** se o pedido não existir.     | **Response:** OrderResponse |


## 5. 🔄 Fluxos do Domínio (Happy Path)

**Fluxo 1**: Criação do Pedido e Início do SAGA

- Cliente envia POST /orders (CreateOrderRequest).
- OrderService salva OrderEntity e OrderItemEntity com status="CREATED".
- OrderService publica OrderCreatedEvent no tópico order-created.
- Outros serviços (inventory-service, payment-service) consomem o evento para prosseguir.

**Fluxo 2**: Atualização de Status (Sucesso do SAGA)

- payment-service publica PaymentProcessedEvent no tópico payment-processed.
- OrderPaymentConsumer consome o evento.
- OrderService atualiza OrderEntity para status="PAID".
- OrderService publica OrderPaidEvent no tópico order-paid (gatilho para envio).

## 6. 🔗 Integrações

**Integrações Entre Serviços**

| Serviço / Sistema            | Tipo de Integração                | Papel no Fluxo                                                                 |
|------------------------------|-----------------------------------|---------------------------------------------------------------------------------|
| **inventory-service**        | Kafka (consumer / producer)       | Consome `order-created` para reservar/diminuir estoque. Produz eventos de falha (SAGA). |
| **payment-service**          | Kafka (consumer / producer)       | Consome `order-created`, processa pagamento e publica `payment-processed` ou `payment-failed`. |
| **Auditoria / Shipping**     | Kafka (consumer)                  | Consome `order-paid` para auditoria, expedição ou emissão de comprovantes.      |

**Kafka Topics Utilizados**


| Tipo       | Tópico             | Descrição                                                                                  |
|------------|--------------------|----------------------------------------------------------------------------------------------|
| **Produzido** | `order-created`     | Inicia o fluxo SAGA. É o gatilho para os serviços de pagamento e estoque.                     |
| **Produzido** | `order-paid`        | Indica sucesso do pedido. Aciona envio, notificação, auditoria, etc.                         |
| **Consumido** | `payment-processed` | Recebido do payment-service para confirmar pagamento e atualizar o pedido como **PAID**.      |
| **Consumido** | `payment-failed`    | Recebido do payment-service para informar falha e atualizar pedido como **PAYMENT_FAILED**.  |


## 7. 🛠️ Dependências do Serviço (padrão)

| Dependência     | Artefato                         | Uso                                                     |
|-----------------|----------------------------------|---------------------------------------------------------|
| Spring Web      | `spring-boot-starter-web`        | APIs REST                                               |
| Spring Kafka    | `spring-kafka`                   | Producers/consumers e desserialização de DTOs          |
| Spring Data JPA | `spring-boot-starter-data-jpa`   | Persistência (`OrderEntity`, `OrderItemEntity`)         |
| H2 Database     | `com.h2database:h2`              | Ambiente local                                           |
| Lombok          | `lombok`                         | Geração de entidades e DTOs                             |
| Actuator        | `spring-boot-starter-actuator`   | Observabilidade                                          |
| Test            | `spring-boot-starter-test`       | Testes (JUnit + Mockito)                                |


## 8. 📦 Eventos (se aplicável)

## Tópicos Kafka do order-service

| Direção   | Tópico             | Payload                 | Quando é emitido / consumido                                           |
|-----------|--------------------|--------------------------|-------------------------------------------------------------------------|
| Produzido | `order-created`    | `OrderCreatedEvent`      | Logo após a persistência inicial do pedido.                            |
| Produzido | `order-paid`       | `OrderPaidEvent`         | Após consumir `payment-processed` e atualizar o status para **PAID**.   |
| Consumido | `payment-processed`| `PaymentProcessedEvent`  | Recebido do payment-service indicando sucesso na cobrança.              |
| Consumido | `payment-failed`   | `PaymentFailedEvent`     | Recebido do payment-service indicando falha na cobrança.                |


## 9. 🧪 Testes

Instruções para rodar testes: **[TO DO]**

```bash
mvn test
```

É essencial ter testes de integração que simulem:

- Criação do pedido e a emissão do evento order-created.
- A reação do OrderPaymentConsumer ao consumir eventos de sucesso (payment-processed) e de falha (payment-failed).

## 10. 🚀 Como rodar o serviço

Local:

Certifique-se de que o servidor Kafka (localhost:9092) esteja em execução.
```bash
./scripts/local-run.sh
```

Docker:
```bash
docker build -t order-service .
docker run -p 8080:8080 order-service
```

## 11. 📚 Logs, Métricas e Observabilidade

**Endpoints de Observabilidade (Actuator)**

| Endpoint            | Função                                                                                      |
|---------------------|----------------------------------------------------------------------------------------------|
| `/actuator/health`  | Exibe o status de saúde do serviço, incluindo verificações de banco de dados e Kafka.       |
| `/actuator/info`    | Mostra informações de build, metadados e configuração do serviço.                           |
| `/actuator/metrics` | Expõe métricas do JVM, transações, banco de dados, latência dos endpoints e métricas Kafka. |

**Componentes de Observabilidade**

| Componente | Função |
|-----------|--------|
| **Prometheus** | Coleta métricas, como taxa de pedidos criados, latência de endpoints, throughput de eventos Kafka e porcentagem de pedidos **PAID vs PAYMENT_FAILED**. |
| **Grafana** | Dashboards para monitorar desempenho do serviço, saúde do fluxo SAGA e métricas de ordem/pagamento. |
| **Loki** | Agregação de logs distribuídos, permitindo rastrear o **orderId** através de todos os eventos (created → processed → paid/failed). |
| **Tempo** | Rastreamento distribuído (Tracing), medindo tempo e correlação do fluxo completo: requisição REST → persistência → envio do evento `order-created` → consumo pelo payment-service. |


## 12. 🧭 Roadmap do Serviço

**Status das Funcionalidades do Order Service**

| Item | Status |
|------|--------|
| Implementação do fluxo de criação e persistência | ✔ Concluído |
| Implementação da orquestração SAGA (eventos `order-created` e `order-paid`/`payment-failed`) | ✔ Concluído |
| Integração real com product-service para buscar preços (remover `unitPrice = 1.0`) | ⏳ Planejado |
| Adicionar compensação (rollback) para o inventory-service em caso de `payment-failed` | ⏳ Planejado |
