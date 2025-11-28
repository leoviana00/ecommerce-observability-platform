package io.viana.order_service.dto;

import lombok.Data; // Gera automaticamente Getters, Setters, toString(), equals() e hashCode()

/**
 * DTO (Data Transfer Object) usado para representar um item individual
 * dentro da lista de itens de um pedido (CreateOrderRequest).
 *
 * Ele transporta as informações necessárias para identificar o produto
 * e a quantidade desejada.
 */
@Data
public class OrderItemRequest {
    
    // ID do produto que está sendo solicitado (referência ao product-service).
    private Long productId;
    
    // Quantidade desejada deste produto no pedido.
    // Essencial para a reserva de estoque (inventory-service).
    private Integer quantity;
}