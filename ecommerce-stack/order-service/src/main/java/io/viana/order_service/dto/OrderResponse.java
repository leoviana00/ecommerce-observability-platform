package io.viana.order_service.dto;

import lombok.Builder; // Permite a criação de objetos usando o padrão Builder
import lombok.Data; // Gera Getters, Setters, toString(), equals() e hashCode()

import java.util.List; // Importa a classe List para a coleção de itens

/**
 * DTO (Data Transfer Object) usado para encapsular a resposta completa
 * de um pedido. É o formato de saída do endpoint GET /orders/{orderId}.
 *
 * Ele representa o estado atual e final do pedido, incluindo o status
 * da transação (PAID, PENDING, FAILED, etc.).
 */
@Data
@Builder
public class OrderResponse {

    // Identificador único do pedido.
    private Long orderId;

    // ID do usuário que fez o pedido.
    private Long userId;

    // Lista detalhada dos itens contidos no pedido.
    // Utiliza o OrderItemResponse para incluir preço unitário e total por item.
    private List<OrderItemResponse> items;

    // Valor total do pedido, calculado a partir da soma dos totais dos itens.
    private Double total;

    // Status atual do pedido (ex: PENDING, PAID, SHIPPED, PAYMENT_FAILED).
    // O status é o indicador mais importante do ciclo de vida do pedido.
    private String status;
}