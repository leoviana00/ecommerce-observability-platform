package io.viana.payment_service.consumer;

import io.viana.payment_service.dto.events.OrderCreatedEvent;
import io.viana.payment_service.service.PaymentProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final PaymentProcessorService paymentService;

    @KafkaListener(
            topics = "order-created",
            groupId = "payment-service-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(OrderCreatedEvent event) {
        log.info("📥 Evento recebido: order-created -> {}", event);
        paymentService.processPayment(event);
    }
}
