package io.viana.payment_service.dto.events;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProcessedEvent {

    private Long orderId;
    private Long userId;
    private Double amount;
    private String transactionId;
    private String status; // APPROVED
    private Long timestamp;
}
