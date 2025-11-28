package io.viana.order_service.dto.events;

import lombok.Builder; // Permite a criação de objetos usando o padrão Builder
import lombok.Data; // Gera automaticamente Getters, Setters, toString(), equals() e hashCode()

/**
 * Evento de Domínio: OrderCreatedEvent
 *
 * Este DTO é emitido pelo 'order-service' assim que um pedido é
 * criado em seu banco de dados com o status "PENDING" ou "CREATED".
 *
 * Ele serve como a **mensagem inicial** para o fluxo SAGA, notificando
 * outros serviços (como Inventory e Payment) para iniciar suas etapas.
 */
@Data
@Builder
public class OrderCreatedEvent {

    // ID do pedido recém-criado. Chave de correlação para a transação distribuída (SAGA).
    private Long orderId;

    // ID do usuário que fez o pedido (útil para antifraude ou personalização).
    private Long userId;

    // Valor total do pedido (crucial para o payment-service).
    private Double total;

    // O momento exato em que o pedido foi criado (útil para auditoria e rastreamento).
    private Long timestamp;
}s