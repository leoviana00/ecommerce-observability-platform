## 📘 Padrão Oficial de Eventos Kafka

Plataforma eCommerce Event-Driven

Este documento define o padrão oficial para implementação de Producers e Consumers Kafka em todos os microserviços da plataforma.

Ele garante que todos os serviços sigam o mesmo modelo, reduzindo divergências técnicas e mantendo a arquitetura consistente e escalável.

## 📁 1. Estrutura de Diretórios

Todo microserviço orientado a eventos deve conter:

```console

src/main/java/.../[service]/
    producer/
        <Domain>EventProducer.java
    consumer/
        <Domain>EventConsumer.java
    dto/
        <EventName>Event.java
    config/
        KafkaConfig.java
```

Exemplos reais:

```console

product-service/
  producer/ProductEventProducer.java
  consumer/ProductEventConsumer.java
  dto/ProductCreatedEvent.java
  config/KafkaConfig.java

inventory-service/
  producer/InventoryEventProducer.java
  consumer/ProductEventConsumer.java
  dto/InventoryUpdatedEvent.java
  dto/InventoryReserveFailedEvent.java
  config/KafkaConfig.java

```

## 🎯 2. Objetivos da Padronização

- Facilitar leitura e manutenção do código
- Garantir clareza entre serviços
- Evitar divergências e duplicação de lógica
- Unificar padrões de logs, nomes de tópicos e estrutura de eventos
- Permitir evolução simples da plataforma com novos serviços

## 🧩 3. Convenção de Nomes

Classe Producer

```console
<Domain>EventProducer
```
- Ex.: `ProductEventProducer`, `OrderEventProducer`

Classe Consumer

```console
<Domain>EventConsumer
```
- Ex.: `ProductEventConsumer`

DTO de Evento

```console
<EventName>Event
```
- Ex.: `ProductCreatedEvent`, `InventoryUpdatedEvent`

Tópicos Kafka

```console
<entity>-<action>
```

- Exempĺos:

| Serviço           | Evento             | Tópico              |
| ----------------- | ------------------ | ------------------- |
| product-service   | produto criado     | `product-created`   |
| inventory-service | estoque atualizado | `inventory-updated` |
| order-service     | pedido criado      | `order-created`     |
| payment-service   | pagamento aprovado | `payment-processed` |

> [!NOTE]
> Regras para nome de tópico:
  - usar kebab-case
  - começar com nome do domínio
  - evitar abreviações

## 📦 4. Padrão de Producers

Estrutura que segui no `product-service`:

```java
@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventProducer {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private static final String TOPIC = "product-created";

    public void sendProductCreated(ProductEntity product, Integer initialStock) {

        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .id(product.getId())
                .name(product.getName())
                .initialStock(initialStock)
                .build();

        kafkaTemplate.send(TOPIC, event);

        log.info("📤 Evento enviado para Kafka: {}", event);
    }
}

```

> [!NOTE]
> Regras:

- A classe deve estar em /producer 
- Nome do tópico como constante
- KafkaTemplate<String, Event> como tipo
- Log com ícone:

```bash
📤 Evento enviado para Kafka: <event>
```
- Evento sempre enviado como DTO
- Nunca enviar entidades JPA diretamente

## 🔧 5. Padrão de Configuração (Producer)

> [!NOTE]
> O arquivo `KafkaConfig.java` deve definir:

  - ProducerFactory
  - KafkaTemplate
  - Serialização JSON sem headers extras

Modelo oficial (aplicado no projeto):

```java
@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, ProductCreatedEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // Remove headers TYPE_INFO para compatibilidade com microserviços poliglotas
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```
> [!TIP]
> Por que remover TYPE_INFO_HEADERS?

  - Evita problemas de desserialização entre serviços escritos em linguagens diferentes.
  - Evita dependência do pacote original da classe.
  - Mantém os eventos mais leves e mais padronizados.

## 📡 6. Padrão de Consumers

Um consumer deve:

  - Residir em /consumer
  - Usar @KafkaListener
  - Declarar claramente qual tópico escuta
  - Usar DTO apropriado
  - Pertencer a um groupId do serviço

Modelo oficial (aplicado no projeto):

```java
@Component
public class ProductEventConsumer {

    @KafkaListener(
        topics = "category-updated",
        groupId = "product-service"
    )
    public void handleCategoryUpdated(ProductEntity product) {
        // Lógica de atualização de categoria
        System.out.println("Evento category-updated recebido: " + product.getName());
    }
}

```
> [!NOTE]
Regras:

  - Todos os consumers devem estar no pacote consumer/
  - groupId deve ser igual ao nome do serviço
  - Usar DTO sempre que possível (evitar entidades do banco)
  - Nunca fazer lógica pesada dentro do listener
  - Preferir delegar para Services (ex: categoryService.update(...))

## 🚦 7. Política de groupId
Cada serviço deve ter um groupId único, seguindo este padrão:

```bash
<service-name>-group
```

Exemplos:

  - product-service-group
  - inventory-service-group
  - order-service-group

## 🛠️ 8. Mapeamento de responsabilidades

Producers

  - Publicam mudanças de estado
  - São acionados após operações do domínio
  - Não devem consumir eventos

Consumers

  - Reagem a eventos externos
  - Não persistem estado a menos que seja necessário
  - Delegam a lógica para services internos

Events (DTOs)

  - São contratos entre microserviços
  - Devem evoluir de forma compatível (backward compatible)
  - Nunca conter entidades JPA
  - Usar timestamp em epoch millis

## 📌 9. Checklist para cada novo microserviço

| Item                                                 | Status |
| ---------------------------------------------------- | ------ |
| Criar pasta `/producer`                              | ✔      |
| Criar pasta `/consumer`                              | ✔      |
| Criar `KafkaConfig` com JsonSerializer sem TYPE_INFO | ✔      |
| Criar DTOs em `/dto`                                 | ✔      |
| Producer com nome `<Domain>EventProducer`            | ✔      |
| Consumer com nome `<Domain>EventConsumer`            | ✔      |
| Tópicos em `<entity>-<action>`                       | ✔      |
| `groupId` consistente                                | ✔      |
| Log padronizado com ícone                            | ✔      |
| Nunca enviar entidade JPA como evento                | ✔      |
