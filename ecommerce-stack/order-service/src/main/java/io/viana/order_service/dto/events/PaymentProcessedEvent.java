package io.viana.order_service.dto.events;

import lombok.*; // Importa todas as anotações do Lombok

/**
 * Evento de Domínio: PaymentProcessedEvent
 *
 * Este DTO é emitido pelo 'payment-service' quando a transação
 * de pagamento é **aprovada com sucesso** pela instituição financeira.
 *
 * Ele é consumido pelo 'order-service' para finalizar a transação SAGA
 * marcando o pedido como "PAID".
 */
@Data // Gera Getters, Setters, toString(), equals() e hashCode().
@NoArgsConstructor // Construtor padrão (necessário para desserialização do Kafka/JSON).
@AllArgsConstructor // Construtor com todos os campos.
@Builder // Permite a criação de objetos usando o padrão Builder.
public class PaymentProcessedEvent {

    // ID do pedido que foi pago. Chave de correlação para a transação.
    private Long orderId;

    // ID do usuário que fez o pedido.
    private Long userId;

    // Valor da transação que foi aprovada.
    private Double amount;

    // Identificador único da transação gerado pelo payment-service ou gateway.
    private String transactionId;

    // Status da operação, tipicamente "APPROVED".
    private String status;

    // Momento em que o pagamento foi aprovado.
    private Long timestamp;
}