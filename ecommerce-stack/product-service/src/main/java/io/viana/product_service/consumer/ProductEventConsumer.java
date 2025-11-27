package io.viana.product_service.consumer;

import org.springframework.kafka.annotation.KafkaListener; // Anotação para definir o método ouvinte (listener) do Kafka
import org.springframework.stereotype.Component; // Permite que o Spring gerencie esta classe como um componente (Bean)

import io.viana.product_service.model.ProductEntity; // Importa o modelo que o payload da mensagem será convertido

/**
 * Classe responsável por consumir (ouvir) eventos de tópicos do Kafka.
 * Em uma arquitetura de microsserviços, ela permite que o 'product-service'
 * reaja de forma assíncrona a eventos de outros serviços (como o 'category-service').
 */
@Component
public class ProductEventConsumer {

    /**
     * Método ouvinte que processa mensagens do tópico "category-updated".
     *
     * @KafkaListener: Configura o método para escutar o tópico.
     * - topics = "category-updated": Nome do tópico que este consumidor monitora.
     * - groupId = "product-service": Identifica o grupo de consumidores. Isso assegura
     * que, se houver múltiplas instâncias deste serviço rodando, cada mensagem
     * deste tópico será processada por apenas UMA delas.
     *
     * @param product O objeto (payload JSON) da mensagem recebida, automaticamente
     * convertido para ProductEntity pelo Spring/Kafka.
     */
    @KafkaListener(topics = "category-updated", groupId = "product-service")
    public void handleCategoryUpdated(ProductEntity product) {
        
        // --- Lógica de Processamento do Evento ---
        
        // 1. Lógica principal: Atualizar a informação de categoria no ProductEntity
        // Exemplo: productRepository.updateCategory(product.getId(), product.getCategoryId());

        // 2. Log de confirmação
        System.out.println("Evento category-updated recebido: Produto: " + product.getName() + ", ID: " + product.getId());
        
        // Nota: O payload recebido (ProductEntity) deve conter o ID e os dados da categoria 
        // necessários para que o serviço de produto possa realizar a atualização.
    }
}