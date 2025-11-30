package io.viana.notification_service.config;

import io.viana.notification_service.dto.events.OrderPaidEvent;
import io.viana.notification_service.dto.events.OrderPaymentFailedEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConfig() {
        Map<String, Object> props = new HashMap<>();

        // CORREÇÃO: remove localhost e usa profiles/env
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-service");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPaidEvent> orderPaidListenerFactory() {
        var deserializer = new JsonDeserializer<>(OrderPaidEvent.class);
        deserializer.addTrustedPackages("*");

        var consumerFactory = new DefaultKafkaConsumerFactory<>(
                baseConfig(),
                new StringDeserializer(),
                deserializer
        );

        ConcurrentKafkaListenerContainerFactory<String, OrderPaidEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, OrderPaymentFailedEvent> orderPaymentFailedListenerFactory() {
        var deserializer = new JsonDeserializer<>(OrderPaymentFailedEvent.class);
        deserializer.addTrustedPackages("*");

        var consumerFactory = new DefaultKafkaConsumerFactory<>(
                baseConfig(),
                new StringDeserializer(),
                deserializer
        );

        ConcurrentKafkaListenerContainerFactory<String, OrderPaymentFailedEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
