package io.viana.product_service.config;

// Importações necessárias para configurar o Kafka Producer
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.*;

import io.viana.product_service.model.ProductEntity; // Importa a classe do modelo que será enviada

import java.util.HashMap;
import java.util.Map;

// Define que esta classe contém configurações para o Spring
@Configuration
public class KafkaConfig {

    /**
     * Define o Bean que configura a fábrica de produtores Kafka.
     * Esta fábrica é responsável por criar instâncias de produtores.
     * @return ProducerFactory configurada para enviar mensagens de ProductEntity.
     */
    @Bean
    public ProducerFactory<String, ProductEntity> producerFactory() {
        // Cria um mapa para armazenar as configurações do produtor
        Map<String, Object> config = new HashMap<>();

        // Configura o endereço do(s) servidor(es) Kafka (bootstrap servers)
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        // Configura o serializador da chave (Key). Usando StringSerializer para chaves de texto.
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Configura o serializador do valor (Value). Usando JsonSerializer do Spring Kafka
        // para converter objetos ProductEntity em JSON antes de enviar ao Kafka.
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, org.springframework.kafka.support.serializer.JsonSerializer.class);

        // Retorna a implementação padrão da fábrica de produtores com as configurações definidas
        return new DefaultKafkaProducerFactory<>(config);
    }

    /**
     * Define o Bean do KafkaTemplate.
     * O KafkaTemplate é uma classe de alto nível usada para enviar mensagens (eventos)
     * para os tópicos do Kafka de forma simples e conveniente.
     * @return KafkaTemplate pronto para uso.
     */
    @Bean
    public KafkaTemplate<String, ProductEntity> kafkaTemplate() {
        // Cria um KafkaTemplate usando a ProducerFactory configurada anteriormente
        return new KafkaTemplate<>(producerFactory());
    }
}