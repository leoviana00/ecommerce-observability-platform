package io.viana.notification_service.dto.events;

import lombok.Data;

@Data
public class OrderPaymentFailedEvent {
    private Long orderId;
    private Long userId;
    private Double amount;
    private String reason;
    private Long timestamp;
}
