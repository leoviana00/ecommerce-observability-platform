package io.viana.product_service.config;

import io.viana.product_service.dto.ProductCreatedEvent;
import org.apache.kafka.clients.producer.ProducerConfig; // Configurações padrão do produtor Kafka
import org.apache.kafka.common.serialization.StringSerializer; // Serializador padrão para a chave da mensagem
import org.springframework.context.annotation.Bean; // Anotação para definir métodos de criação de beans
import org.springframework.context.annotation.Configuration; // Define a classe como uma fonte de definição de beans
import org.springframework.kafka.core.DefaultKafkaProducerFactory; // Implementação padrão de fábrica de produtores
import org.springframework.kafka.core.KafkaTemplate; // Classe central para enviar mensagens Kafka
import org.springframework.kafka.core.ProducerFactory; // Interface para criação de instâncias de Produtores
import org.springframework.kafka.support.serializer.JsonSerializer; // Serializador do Spring para converter objetos Java em JSON

import java.util.HashMap;
import java.util.Map;

/**
 * Classe de configuração do Spring responsável por configurar
 * e criar os beans necessários para atuar como Produtor Kafka.
 */
@Configuration
public class KafkaConfig {

    /**
     * Define o bean de fábrica que será usado para criar instâncias de Produtores Kafka.
     * Esta fábrica encapsula as configurações de conexão e serialização.
     *
     * @return Uma fábrica de Produtores configurada.
     */
    @Bean
    public ProducerFactory<String, ProductCreatedEvent> producerFactory() {
        Map<String, Object> props = new HashMap<>();

        // 1. Endereço do Servidor Kafka
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // 2. Serializadores
        // Define como a CHAVE da mensagem será convertida em bytes
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // Define como o VALOR (payload) da mensagem (ProductCreatedEvent) será convertido em bytes (JSON)
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        // 3. Configuração do JsonSerializer
        // Configuração importante: Desabilita o cabeçalho "__TypeId__" (TYPE_INFO).
        // Isso torna o payload JSON mais limpo e facilita a interoperabilidade
        // com outros consumidores escritos em diferentes linguagens (poliglotas).
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Define o bean principal do KafkaTemplate.
     * Esta é a classe que o ProductEventProducer injeta e usa para enviar mensagens.
     *
     * @return Uma instância de KafkaTemplate configurada com a ProducerFactory.
     */
    @Bean
    public KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}