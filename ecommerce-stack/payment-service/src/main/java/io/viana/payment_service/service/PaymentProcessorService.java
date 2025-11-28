package io.viana.payment_service.service;

import io.viana.payment_service.dto.events.*;
import io.viana.payment_service.producer.PaymentEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentProcessorService {

    private final PaymentEventProducer producer;

    public void processPayment(OrderCreatedEvent event) {

        log.info("💳 Processando pagamento para orderId={} | valor={}",
                event.getOrderId(), event.getTotal());

        boolean approved = simulateGateway();

        if (approved) {
            PaymentProcessedEvent result = PaymentProcessedEvent.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .amount(event.getTotal())
                    .transactionId(UUID.randomUUID().toString())
                    .status("APPROVED")
                    .timestamp(System.currentTimeMillis())
                    .build();

            producer.sendPaymentProcessed(result);
            log.info("✔ Pagamento aprovado: {}", result);
            return;
        }

        PaymentFailedEvent failed = PaymentFailedEvent.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(event.getTotal())
                .reason("CARD_DENIED")
                .status("FAILED")
                .timestamp(System.currentTimeMillis())
                .build();

        producer.sendPaymentFailed(failed);
        log.warn("❌ Pagamento recusado: {}", failed);
    }

    private boolean simulateGateway() {
        return Math.random() > 0.2; // 80% aprovado
    }
}
