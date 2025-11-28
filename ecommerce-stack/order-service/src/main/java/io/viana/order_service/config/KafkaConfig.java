package io.viana.order_service.config;

import io.viana.order_service.dto.events.PaymentProcessedEvent; // DTO para evento de pagamento processado
import io.viana.order_service.dto.events.PaymentFailedEvent; // DTO para evento de pagamento falhado
import org.apache.kafka.clients.consumer.ConsumerConfig; // Configurações do consumidor
import org.apache.kafka.clients.producer.ProducerConfig; // Configurações do produtor

import org.apache.kafka.common.serialization.StringDeserializer; // Deserializador de chave
import org.apache.kafka.common.serialization.StringSerializer; // Serializador de chave

import org.springframework.context.annotation.Bean; // Definição de beans
import org.springframework.context.annotation.Configuration; // Marca como classe de configuração
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory; // Fábrica de listeners
import org.springframework.kafka.core.*; // Interfaces e classes Core Kafka

import org.springframework.kafka.support.serializer.JsonDeserializer; // Deserializador JSON
import org.springframework.kafka.support.serializer.JsonSerializer; // Serializador JSON

import java.util.HashMap;
import java.util.Map;

/**
 * Classe de configuração para Produtor e Consumidores Kafka no order-service.
 * O serviço precisa tanto enviar eventos (pedidos) quanto consumir eventos (pagamento).
 */
@Configuration
public class KafkaConfig {

    // --- 1. Configurações do Produtor (Genérico para qualquer DTO) ---

    /**
     * Fábrica de Produtores Kafka.
     * Configurada para enviar String como chave e qualquer Object como valor (serializado em JSON).
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String,Object> props = new HashMap<>();
        // Endereço do servidor Kafka
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Serializadores
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // Desabilita cabeçalhos de tipo para interoperabilidade (melhor prática em microsserviços)
        props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false); 
        return new DefaultKafkaProducerFactory<>(props);
    }

    /**
     * Template Kafka principal. Usado para enviar todas as mensagens do Produtor.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // --- 2. Configurações do Consumidor (Específico para PaymentProcessedEvent) ---

    /**
     * Fábrica de Consumidores para desserializar o evento PaymentProcessedEvent.
     */
    @Bean
    public ConsumerFactory<String, PaymentProcessedEvent> paymentProcessedConsumerFactory() {
        // Deserializador JSON configurado para converter o JSON de entrada para a classe PaymentProcessedEvent
        JsonDeserializer<PaymentProcessedEvent> deserializer =
                new JsonDeserializer<>(PaymentProcessedEvent.class);
        // Permite desserializar classes de qualquer pacote (importante para eventos)
        deserializer.addTrustedPackages("*");

        Map<String,Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // ID do grupo de consumidores. Garante que cada evento seja processado apenas uma vez pelo order-service.
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service-payment");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(), // Deserializador de chave
                deserializer // Deserializador de valor configurado
        );
    }

    /**
     * Fábrica de Listener para PaymentProcessedEvent.
     * Usada pelo @KafkaListener para criar o contêiner de execução.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentProcessedEvent> paymentProcessedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentProcessedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentProcessedConsumerFactory());
        return factory;
    }

    // --- 3. Configurações do Consumidor (Específico para PaymentFailedEvent) ---

    /**
     * Fábrica de Consumidores para desserializar o evento PaymentFailedEvent.
     */
    @Bean
    public ConsumerFactory<String, PaymentFailedEvent> paymentFailedConsumerFactory() {
        // Deserializador JSON configurado para converter o JSON de entrada para a classe PaymentFailedEvent
        JsonDeserializer<PaymentFailedEvent> deserializer =
                new JsonDeserializer<>(PaymentFailedEvent.class);
        deserializer.addTrustedPackages("*");

        Map<String,Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        // Compartilha o mesmo grupo ID com o consumidor de sucesso, pois ambos reagem ao fluxo de pagamento.
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "order-service-payment");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer);

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    /**
     * Fábrica de Listener para PaymentFailedEvent.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> paymentFailedListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PaymentFailedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(paymentFailedConsumerFactory());
        return factory;
    }
}