package io.viana.payment_service.producer;

import io.viana.payment_service.dto.events.PaymentFailedEvent;
import io.viana.payment_service.dto.events.PaymentProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC_PAYMENT_PROCESSED = "payment-processed";
    private static final String TOPIC_PAYMENT_FAILED = "payment-failed";

    public void sendPaymentProcessed(PaymentProcessedEvent event) {
        kafkaTemplate.send(TOPIC_PAYMENT_PROCESSED, event);
        log.info("📤 Enviado evento payment-processed: {}", event);
    }

    public void sendPaymentFailed(PaymentFailedEvent event) {
        kafkaTemplate.send(TOPIC_PAYMENT_FAILED, event);
        log.info("📤 Enviado evento payment-failed: {}", event);
    }
}
