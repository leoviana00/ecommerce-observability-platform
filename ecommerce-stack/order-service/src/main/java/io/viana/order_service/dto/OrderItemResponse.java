package io.viana.order_service.dto;

import lombok.Builder; // Permite a criação de objetos usando o padrão Builder
import lombok.Data; // Gera Getters, Setters, toString(), equals() e hashCode()

/**
 * DTO (Data Transfer Object) usado para representar um item individual
 * dentro da lista de itens de um pedido na resposta da API (OrderResponse).
 *
 * Ele contém os dados do OrderItem no DB, enriquecido com informações
 * financeiras (preço unitário e total do item).
 */
@Data
@Builder
public class OrderItemResponse {

    // ID do produto que foi pedido.
    private Long productId;

    // Quantidade comprada deste produto.
    private Integer quantity;

    // Preço unitário do produto no momento da compra (snapshot do preço).
    private Double unitPrice;

    // Valor total do item (quantity * unitPrice).
    private Double total;
}