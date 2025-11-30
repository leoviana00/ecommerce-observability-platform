package io.viana.notification_service.consumer;

import io.viana.notification_service.dto.events.OrderPaidEvent;
import io.viana.notification_service.dto.events.OrderPaymentFailedEvent;
import io.viana.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    /**
     * Consome eventos de pagamento APROVADO.
     */
    @KafkaListener(
            topics = "order-paid",
            groupId = "notification-service",
            containerFactory = "orderPaidListenerFactory"
    )
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("📥 Evento recebido: order-paid -> {}", event);
        notificationService.notifyPaymentSuccess(event);
    }

    /**
     * Consome eventos de pagamento RECUSADO.
     * Corrigido: este evento vem do payment-service, tópico payment-failed
     */
    @KafkaListener(
            topics = "payment-failed",
            groupId = "notification-service",
            containerFactory = "orderPaymentFailedListenerFactory"
    )
    public void handlePaymentFailed(OrderPaymentFailedEvent event) {
        log.info("📥 Evento recebido: payment-failed -> {}", event);
        notificationService.notifyPaymentFailed(event);
    }
}
