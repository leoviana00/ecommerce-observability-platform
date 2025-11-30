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

        // =====================================================
        // 🔥 Regra especial: userId=999 sempre falha (testes)
        // =====================================================
        if (event.getUserId() == 999) {
            PaymentFailedEvent failed = PaymentFailedEvent.builder()
                    .orderId(event.getOrderId())
                    .userId(event.getUserId())
                    .amount(event.getTotal())
                    .reason("SIMULATED_TEST_FAILURE")
                    .status("FAILED")
                    .timestamp(System.currentTimeMillis())
                    .build();

            producer.sendPaymentFailed(failed);
            log.warn("❌ Pagamento recusado (simulação userId=999): {}", failed);
            return;
        }

        // =====================================================
        // 💰 Lógica normal do gateway (80% aprovado)
        // =====================================================
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

        // =====================================================
        // ❌ Pagamento negado aleatoriamente
        // =====================================================
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

    // 80% aprovado
    private boolean simulateGateway() {
        return Math.random() > 0.2;
    }
}
