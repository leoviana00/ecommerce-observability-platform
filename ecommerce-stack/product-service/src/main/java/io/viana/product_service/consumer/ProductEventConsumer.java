package io.viana.product_service.consumer;

import org.springframework.kafka.annotation.KafkaListener; // Importa a anotação para definir o método ouvinte (listener) do Kafka
import org.springframework.stereotype.Component; // Importa a anotação para que o Spring gerencie esta classe como um componente

import io.viana.product_service.model.ProductEntity; // Importa o modelo de dados esperado (ProductEntity)

/**
 * Define a classe responsável por consumir (ouvir) eventos de tópicos do Kafka.
 * O Spring a reconhece como um componente gerenciável.
 */
@Component
public class ProductEventConsumer {

    /**
     * Este método é o ouvinte real (listener) que processa mensagens do Kafka.
     * * @KafkaListener: Anotação que configura este método para escutar mensagens.
     * - topics = "category-updated": Especifica o nome do tópico que este consumidor irá ouvir.
     * (Exemplo: O 'category-service' pode enviar eventos para este tópico quando uma categoria muda).
     * - groupId = "product-service": Define o grupo de consumidores. Todos os consumidores com o 
     * mesmo groupId atuam como um único serviço, garantindo que cada mensagem seja
     * processada por apenas uma instância dentro deste grupo.
     * * @param product: O objeto (payload) da mensagem recebida, que o Spring converte automaticamente
     * para um ProductEntity (desde que o produtor tenha enviado JSON).
     */
    @KafkaListener(topics = "category-updated", groupId = "product-service")
    public void handleCategoryUpdated(ProductEntity product) {
        // Aqui você processa eventos externos
        
        // 1. Lógica principal: Atualizar a informação de categoria no ProductEntity
        // Exemplo: Buscar a categoria atualizada e aplicá-la ao produto.
        // productRepository.updateCategory(product.getId(), product.getCategoryId());

        // 2. Log para debug ou confirmação
        System.out.println("Evento category-updated recebido: " + product.getName());
    }
}