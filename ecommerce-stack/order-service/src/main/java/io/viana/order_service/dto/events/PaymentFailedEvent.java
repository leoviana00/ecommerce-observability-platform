package io.viana.order_service.dto.events;

import lombok.*; // Importa todas as anotações do Lombok

/**
 * Evento de Domínio: PaymentFailedEvent
 *
 * Este DTO é emitido pelo 'payment-service' quando a transação
 * é **rejeitada** (ex: cartão inválido, saldo insuficiente).
 *
 * Ele notifica o 'order-service' e outros sistemas (como o Inventory)
 * para iniciar a **compensação** e reverter o estado da transação.
 */
@Data // Gera Getters, Setters, toString(), equals() e hashCode().
@NoArgsConstructor // Construtor padrão (necessário para desserialização do Kafka/JSON).
@AllArgsConstructor // Construtor com todos os campos.
@Builder // Permite a criação de objetos usando o padrão Builder.
public class PaymentFailedEvent {

    // ID do pedido que tentou ser pago. Chave de correlação para a transação.
    private Long orderId;

    // ID do usuário que fez o pedido.
    private Long userId;

    // Valor da transação que falhou.
    private Double amount;

    // Detalhe da falha (ex: "CARD_DECLINED", "INSUFFICIENT_FUNDS").
    private String reason;

    // Status da operação, tipicamente "FAILED".
    private String status; 

    // Momento em que a falha foi registrada.
    private Long timestamp;
}