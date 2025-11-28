package io.viana.inventory_service.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig; // Configurações padrão do consumidor Kafka
import org.apache.kafka.common.serialization.StringDeserializer; // Deserializador padrão para a chave da mensagem
import org.springframework.context.annotation.Bean; // Anotação para definir métodos de criação de beans
import org.springframework.context.annotation.Configuration; // Define a classe como uma fonte de definição de beans

import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory; // Fábrica para criar contêineres de listener concorrentes
import org.springframework.kafka.core.ConsumerFactory; // Interface para criação de instâncias de Consumidores
import org.springframework.kafka.core.DefaultKafkaConsumerFactory; // Implementação padrão de fábrica de consumidores

import org.springframework.kafka.support.serializer.JsonDeserializer; // Deserializador do Spring para converter JSON em objetos Java

import io.viana.inventory_service.dto.ProductCreatedEvent; // O DTO que será desserializado

/**
 * Classe de configuração do Spring responsável por configurar
 * e criar os beans necessários para que o 'inventory-service' atue como Consumidor Kafka.
 */
@Configuration
public class KafkaConsumerConfig {

    /**
     * Define o bean de fábrica que será usado para criar instâncias de Consumidores Kafka.
     * Esta fábrica encapsula as configurações de conexão e desserialização.
     *
     * @return Uma fábrica de Consumidores configurada.
     */
    @Bean
    public ConsumerFactory<String, ProductCreatedEvent> consumerFactory() {
        Map<String, Object> config = new HashMap<>();

        // 1. Endereço do Servidor Kafka
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        
        // 2. ID do Grupo de Consumidores
        // Essencial para o rastreamento de offsets e para o modelo de entrega de mensagens (garante que
        // cada mensagem seja processada apenas uma vez pelo grupo).
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "inventory-product-created-group");

        // 3. Deserializadores
        // Define como a CHAVE da mensagem (String) será convertida de bytes
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Define o deserializador para o VALOR (payload) da mensagem (JSON)
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        // Cria uma instância específica do JsonDeserializer
        // Ele precisa saber o tipo de destino para a conversão.
        JsonDeserializer<ProductCreatedEvent> deserializer =
                new JsonDeserializer<>(ProductCreatedEvent.class);
        
        // Permite a desserialização de classes vindas de pacotes não explicitamente confiáveis.
        // Necessário quando o produtor e o consumidor estão em pacotes/módulos diferentes.
        deserializer.addTrustedPackages("*");

        // Retorna a fábrica de consumidores usando os deserializadores específicos
        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(), // Deserializador de chave
                deserializer // Deserializador de valor configurado
        );
    }

    /**
     * Define o bean da Fábrica de Contêineres de Listener.
     * Esta fábrica é usada pela anotação @KafkaListener para criar o ambiente
     * onde os métodos de escuta (listeners) serão executados.
     *
     * @return Uma fábrica de contêineres que usará o consumerFactory configurado.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ProductCreatedEvent> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, ProductCreatedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        // Associa esta fábrica ao ConsumerFactory que definimos acima
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}