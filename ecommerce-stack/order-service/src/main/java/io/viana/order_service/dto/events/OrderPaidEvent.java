package io.viana.order_service.dto.events;

import lombok.Builder; // Permite a criação de objetos usando o padrão Builder
import lombok.Data; // Gera automaticamente Getters, Setters, toString(), equals() e hashCode()

/**
 * Evento de Domínio: OrderPaidEvent
 *
 * Este DTO é emitido pelo 'order-service' *após* consumir um evento de
 * sucesso do 'payment-service' e atualizar o status do pedido para PAID.
 *
 * Sua principal função é notificar serviços downstream (ex: Shipping, Notification)
 * que o pedido está pronto para ser processado.
 */
@Data
@Builder
public class OrderPaidEvent {

    // ID do pedido que foi pago.
    private Long orderId;

    // ID do usuário que fez o pedido.
    private Long userId;

    // Valor total do pagamento.
    private Double amount;

    // Status final do pedido. Geralmente será "PAID" quando este evento for emitido.
    private String status; 

    // Mensagem descritiva (ex: "Pagamento confirmado").
    private String message;
}