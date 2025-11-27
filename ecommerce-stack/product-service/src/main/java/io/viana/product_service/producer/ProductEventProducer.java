package io.viana.product_service.producer;

// Importações necessárias
import io.viana.product_service.dto.ProductCreatedEvent;
import io.viana.product_service.model.ProductEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// Habilita o logging
@Slf4j
// Marca a classe como um componente Spring, permitindo que seja injetada
@Component
// Gera um construtor para injeção de dependência do KafkaTemplate
@RequiredArgsConstructor
public class ProductEventProducer {

    // Injeção de dependência do KafkaTemplate
    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    
    // Nome do tópico do Kafka
    private static final String TOPIC = "product-created";

    /**
     * Envia um evento de criação de produto para o tópico do Kafka.
     */
    public void sendProductCreated(ProductEntity product, Integer initialStock) {

        // 1. Cria o objeto de evento (DTO) a partir dos dados do produto
        ProductCreatedEvent event = ProductCreatedEvent.builder()
                .id(product.getId())
                .name(product.getName())
                .initialStock(initialStock)
                .build();

        // 2. Envia a mensagem (evento) para o tópico especificado no Kafka
        kafkaTemplate.send(TOPIC, event);

        // 3. Loga a informação de que o evento foi enviado
        log.info("📤 Evento enviado para Kafka: {}", event);
    }
}