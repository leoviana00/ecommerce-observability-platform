package io.viana.monitor_state_manager.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Publica eventos de mudança de estado em um tópico Kafka.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaStatePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${state-events.topic}")
    private String topic;

    public void publish(String service, String json) {
        kafkaTemplate.send(topic, service, json)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish state change for {} to Kafka", service, ex);
                    } else {
                        log.info("Published state change to Kafka: service={} topic={}", service, topic);
                    }
                });
    }
}
