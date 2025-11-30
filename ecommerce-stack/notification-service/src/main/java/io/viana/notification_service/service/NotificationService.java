package io.viana.notification_service.service;

import io.viana.notification_service.dto.events.OrderPaidEvent;
import io.viana.notification_service.dto.events.OrderPaymentFailedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void notifyPaymentSuccess(OrderPaidEvent event) {
        log.info("📢 Notificação enviada: PAGAMENTO APROVADO");
        log.info(" -> orderId: {}", event.getOrderId());
        log.info(" -> userId: {}", event.getUserId());
        log.info(" -> amount: {}", event.getAmount());
    }

    public void notifyPaymentFailed(OrderPaymentFailedEvent event) {
        log.info("📢 Notificação enviada: PAGAMENTO FALHOU");
        log.info(" -> orderId: {}", event.getOrderId());
        log.info(" -> userId: {}", event.getUserId());
        log.info(" -> reason: {}", event.getReason());
    }
}
