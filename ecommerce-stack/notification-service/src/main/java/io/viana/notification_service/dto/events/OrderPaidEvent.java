package io.viana.notification_service.dto.events;

import lombok.Data;

@Data
public class OrderPaidEvent {
    private Long orderId;
    private Long userId;
    private Double amount;
    private Long timestamp;
}
