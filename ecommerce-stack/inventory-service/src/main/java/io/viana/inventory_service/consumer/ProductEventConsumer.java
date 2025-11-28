package io.viana.inventory_service.consumer;

import org.springframework.kafka.annotation.KafkaListener; // Anotação para configurar o método ouvinte
import org.springframework.stereotype.Component; // Permite que o Spring gerencie esta classe

import io.viana.inventory_service.dto.ProductCreatedEvent; // DTO do evento que será consumido
import io.viana.inventory_service.service.InventoryService; // Serviço de negócio para manipular o estoque

/**
 * Classe responsável por ouvir e processar eventos do Kafka.
 * O 'inventory-service' reage a eventos de 'product-service' para manter
 * seus dados de estoque sincronizados.
 */
@Component
public class ProductEventConsumer {

    // Injeção do serviço onde a lógica de negócio real para o estoque reside.
    private final InventoryService inventoryService;

    /**
     * Construtor para injeção de dependência do InventoryService.
     */
    public ProductEventConsumer(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    /**
     * Método ouvinte do Kafka que reage à criação de um novo produto.
     *
     * @KafkaListener: Configura o método para escutar um tópico específico.
     * - topics = "product-created": O tópico do qual as mensagens serão lidas.
     * - groupId = "inventory-product-created-group": Identificador do grupo de consumidores.
     *
     * @param event O payload da mensagem, automaticamente desserializado para ProductCreatedEvent.
     */
    @KafkaListener(
        topics = "product-created",
        groupId = "inventory-product-created-group"
    )
    public void consume(ProductCreatedEvent event) {
        // 1. Log da mensagem recebida (opcional, mas recomendado)
        System.out.println("Evento 'product-created' recebido para o Produto ID: " + event.getId());

        // 2. Chama a lógica de negócio:
        // O serviço de inventário utiliza os dados do evento (ID e initialStock)
        // para criar o registro inicial de estoque em seu próprio banco de dados.
        inventoryService.createInitialStock(event);
    }
}