## 📦 inventory-service
Microserviço gerado automaticamente pelo script generate-service.sh. Este projeto segue o padrão arquitetural SkyFolder utilizado por toda a plataforma.

## 1. 🎯 Objetivo do Serviço
O propósito principal do inventory-service é controlar o estoque de todos os produtos do catálogo. Ele é o único sistema de registro responsável por manter a quantidade atual de cada produto.

## 2. 🧠 Responsabilidades do Serviço
- Gerenciar a quantidade de estoque por product_id. 
- Reagir à criação de novos produtos (ProductCreatedEvent) para inicializar o estoque. 
- Validar a disponibilidade de estoque para operações de redução (ex: pedidos). 
- Publicar eventos de sucesso (InventoryUpdatedEvent) e falha (InventoryReserveFailedEvent) para garantir a consistência distribuída (SAGA).
- Expor endpoints REST para consulta de disponibilidade e movimentação de estoque.

### ❌ O que este serviço não faz (para evitar confusão):

- Não mantém dados de catálogo (nome, descrição, preço). Essa é a responsabilidade do product-service.
- Não orquestra o fluxo de pedidos; apenas atende à requisição de reserva/diminuição de estoque.

Não mantém o histórico completo de pedidos; apenas o registro da quantidade atual.

## 3. 🏗️ Arquitetura Interna (SkyFolder)
Estrutura padronizada do microserviço:
```bash
src/main/java/io/viana/inventory_service/
  ├── controller/       // APIs REST (consulta e movimentação)
  ├── service/          // Lógica de negócio, validação e orquestração de eventos
  ├── repository/       // Spring Data JPA para acesso ao InventoryEntity
  ├── model/            // Entidade JPA: InventoryEntity
  ├── dto/              // DTOs de Request, Response e Eventos Kafka
  ├── producer/         // Componente para envio de eventos Kafka
  ├── consumer/         // Componente para consumo de eventos Kafka (Ex: ProductCreatedEvent)
  ├── config/           // Configuração de Beans (Ex: KafkaConsumerConfig)
  └── exception/        // Exceções customizadas (ProductNotFound, InsufficientStock)
src/main/resources/
```

## 4. 📡 APIs Principais
Documente aqui os endpoints expostos pelo serviço.
```bash
GET /inventory/{productId}
```
- Descrição: Retorna o status de estoque de um produto. Usado por serviços de consulta. Retorno: InventoryResponse (com stockQuantity e isAvailable).

```bash
POST /inventory/{productId}/increase
```
- Descrição: Aumenta a quantidade de estoque. Usado para reposição (RESTOCK) ou ajuste. Corpo: MovementRequest (contém amount e reason).

```bash
POST /inventory/{productId}/decrease
```
- Descrição: Diminui (reserva) a quantidade de estoque. Usado tipicamente pelo order-service. Corpo: MovementRequest (contém amount e reason). Retorna 409 Conflict em caso de estoque insuficiente.

## 5. 🔄 Fluxos do Domínio (Happy Path)

**Fluxo 1**: Inicialização do Estoque (Assíncrono)

- product-service cria um produto.
- product-service emite ProductCreatedEvent para o tópico product-created.
- inventory-service consome o evento.
- InventoryService cria um novo InventoryEntity no DB com a initialStock.

**Fluxo 2**: Reserva de Estoque (Síncrono e Assíncrono)

- order-service chama: POST /inventory/{productId}/decrease.
- InventoryService valida se requested < stockQuantity.

> [!NOTE]
>Se OK: a. Atualiza o InventoryEntity (stockQuantity = stockQuantity - requested). b. Publica InventoryUpdatedEvent no tópico inventory-updated. c. Retorna 200 OK.

## 6. 🔗 Integrações

- **Liste dependências externas**

| Serviço / Sistema           | Tipo de Integração        | Papel no Fluxo                                                                 |
|-----------------------------|----------------------------|--------------------------------------------------------------------------------|
| **product-service**         | Kafka (consumer)           | Fonte de verdade para criação/atualização de produtos (evento `product-created`). |
| **order-service**           | REST (cliente principal)   | Responsável por solicitar reserva, baixa ou validação de estoque.              |
| **Auditoria / Notificação** | Kafka (consumer)           | Consumidores do evento `inventory-updated` para logs, alertas ou notificações. |


- **Kafka Topics Utilizados**

| Tipo           | Tópico                     | Descrição                                                                                     |
|----------------|-----------------------------|-----------------------------------------------------------------------------------------------|
| **Consumido**  | `product-created`           | Inicializa o estoque assim que um novo produto é criado no product-service.                  |
| **Produzido**  | `inventory-updated`         | Notifica serviços downstream sobre alterações no estoque (baixa, restauração ou ajustes).    |
| **Produzido**  | `inventory-reserve-failed`  | Indica falha na reserva de estoque, utilizado para mecanismos de compensação (SAGA).         |


## 7. 🛠️ Dependências do Serviço (padrão)

| Dependência        | Artefato                         | Uso                                               |
|--------------------|----------------------------------|----------------------------------------------------|
| Spring Web         | spring-boot-starter-web          | APIs REST                                          |
| Spring Kafka       | spring-kafka                     | Producers/consumers e desserialização de DTOs      |
| Spring Data JPA    | spring-boot-starter-data-jpa     | Persistência (com @Transactional)                  |
| H2 Database        | com.h2database:h2                | Ambiente local                                     |
| Lombok             | lombok                           | Geração de entidades e DTOs                        |
| Actuator           | spring-boot-starter-actuator     | Observabilidade                                    |
| Test               | spring-boot-starter-test         | Testes (JUnit + Mockito)                           |



## 8. 📦 Eventos (se aplicável)

**Eventos Kafka — Inventory Service**

| Direção        | Tópico                     | Payload                         | Quando é emitido/consumido                                                   |
|----------------|-----------------------------|----------------------------------|------------------------------------------------------------------------------|
| **Produzido**  | `inventory-updated`         | `InventoryUpdatedEvent`          | Em qualquer alteração bem-sucedida de estoque (increase/decrease).          |
| **Produzido**  | `inventory-reserve-failed`  | `InventoryReserveFailedEvent`    | Quando a tentativa de diminuir estoque falha por quantidade insuficiente.   |
| **Consumido**  | `product-created`           | `ProductCreatedEvent`            | Usado para criar o registro inicial do `InventoryEntity` para um novo item. |


## 9. 🧪 Testes

Instruções para rodar testes: **Testes ainda não implementados**

```bash
mvn test
```

A camada de Service exige testes de unidade robustos para cobrir os fluxos de sucesso, as exceções (InsufficientStockException) e a emissão correta dos eventos Kafka. [**TO DO**]

## 10. 🚀 Como rodar o serviço
Local:
Certifique-se de que o servidor Kafka (localhost:9092) esteja em execução.
```bash
./scripts/local-run.sh
```

Docker:
```bash
docker build -t inventory-service .
docker run -p 8080:8080 inventory-service
```

## 11. 📚 Logs, Métricas e Observabilidade

**Endpoints padrão**


| Endpoint             | Função                                                            |
|----------------------|-------------------------------------------------------------------|
| **/actuator/health** | Exibe o status de saúde do serviço, incluindo verificação de DB e Kafka (healthcheck). |
| **/actuator/info**   | Mostra informações de build, versão, ambiente e metadados do serviço. |
| **/actuator/metrics**| Expõe métricas do JVM, Garbage Collector, transações, DB, threads, CPU e métricas Kafka. |



**Componentes de Observabilidade**

| Componente   | Função                                                                 |
|--------------|------------------------------------------------------------------------|
| **Prometheus** | Coleta de métricas de todos os serviços, incluindo latência de endpoints, métricas do JVM e contagem de eventos Kafka. |
| **Grafana**    | Visualização centralizada das métricas coletadas; dashboards para tráfego, latência, Kafka e saúde dos microsserviços. |
| **Loki**       | Agregação e centralização dos logs distribuídos. Fundamental para rastrear o ciclo de vida dos eventos Kafka (product-created, order-created, payment-processed etc.). |
| **Tempo**      | Rastreamento distribuído (Tracing). Permite acompanhar a jornada completa de uma requisição passando por múltiplos microsserviços, identificando gargalos de latência. |


## 12. 🧭 Roadmap do Serviço
**Status das Funcionalidades do Inventory Service**

| Item                                                       | Status      |
|-----------------------------------------------------------|-------------|
| Implementação completa do CRUD de estoque                 | ✔ Concluído |
| Publicação de eventos de sucesso e falha (SAGA)           | ✔ Concluído |
| Adicionar rastreamento de estoque por armazém (Warehouse) | ⏳ Planejado |
| Integrar com um sistema de reserva otimista (Locking)     | ⏳ Planejado |




## 13. 📝 Notas adicionais

### Considerações de Confiabilidade e Consistência

### Idempotência e Resiliência a Falhas de Rede
O `inventory-service` foi projetado para operar de forma robusta em cenários de falhas temporárias na rede ou em duplicação de mensagens provenientes do Kafka.  
O método **`createInitialStock`** é **idempotente**, garantindo que múltiplas entregas do evento `product-created` não resultem em duplicação de registros de estoque.

Essa abordagem assegura:
- Criação segura do estoque inicial mesmo em caso de *retries* automáticos do Kafka.
- Compatibilidade com sistemas distribuídos e semântica *at-least-once*.

---

### Consistência Transacional e Concorrência
O campo **`stockQuantity`** em `InventoryEntity` **sempre** deve ser lido e modificado dentro de métodos anotados com `@Transactional`.

Motivações:
- Garantir atomicidade das operações de aumento e diminuição de estoque.
- Prevenir condições de corrida em ambientes de múltiplos consumidores.
- Assegurar integridade dos dados ao lidar com reservas de estoque em fluxos de pedido (SAGA).
- Permitir que o JPA faça locking transacional explícito quando necessário.

Regras adotadas:
- Nenhum método deve alterar `stockQuantity` fora de contexto transacional.
- Chamadas de negócio como `increaseStock(...)`, `decreaseStock(...)` e `reserveStock(...)` são sempre `@Transactional` no nível de serviço.
- O repositório nunca deve expor métodos que ajustem estoque diretamente via queries “soltas” sem transação.


