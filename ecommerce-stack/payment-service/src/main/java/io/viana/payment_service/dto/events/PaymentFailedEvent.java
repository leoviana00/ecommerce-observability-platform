package io.viana.payment_service.dto.events;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentFailedEvent {

    private Long orderId;
    private Long userId;
    private Double amount;
    private String reason; // ex: "CARD_DENIED", "INSUFFICIENT_FUNDS"
    private String status; // FAILED
    private Long timestamp;
}
