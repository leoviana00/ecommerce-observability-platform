package io.viana.product_service.producer;

import org.springframework.kafka.core.KafkaTemplate; // Importa a classe principal para enviar mensagens ao Kafka
import org.springframework.stereotype.Component; // Marca a classe para ser gerenciada pelo Spring

import io.viana.product_service.model.ProductEntity; // Importa o modelo de dados a ser enviado

/**
 * Componente que encapsula toda a lógica de produção (envio) de eventos para o Kafka.
 * Isso mantém o ProductService limpo e focado apenas na lógica de negócio.
 */
@Component
public class ProductEventProducer {

    // O KafkaTemplate é a ferramenta do Spring para interagir com o Kafka.
    private final KafkaTemplate<String, ProductEntity> kafkaTemplate;

    /**
     * Construtor da classe.
     * O Spring injeta automaticamente o KafkaTemplate configurado (visto no KafkaConfig)
     * via Injeção de Dependência (DI).
     * @param kafkaTemplate A instância do template Kafka injetada pelo Spring.
     */
    public ProductEventProducer(KafkaTemplate<String, ProductEntity> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Método responsável por enviar um evento de 'Produto Criado' para o Kafka.
     * @param product A entidade ProductEntity que foi recém-criada ou atualizada.
     */
    public void sendProductCreated(ProductEntity product) {
        // 1. "product-created": É o nome do tópico Kafka para onde a mensagem será enviada.
        // 2. product: É o payload (valor) da mensagem, que será serializado (JSON, no nosso caso).
        // NOTA: Para um sistema mais robusto, é recomendado usar product.getId().toString() como a chave (key)
        // para garantir o ordenamento na mesma partição (se necessário).
        kafkaTemplate.send("product-created", product);
    }
}