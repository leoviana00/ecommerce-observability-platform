package io.viana.order_service.dto;

import lombok.Data; // Gera automaticamente Getters, Setters, toString(), equals() e hashCode()
import java.util.List; // Importa a classe List para a coleção de itens

/**
 * DTO (Data Transfer Object) usado para receber o payload JSON
 * do cliente quando um novo pedido é submetido (POST /orders).
 * Ele encapsula todas as informações necessárias para iniciar o processo SAGA.
 */
@Data
public class CreateOrderRequest {
    
    // ID do usuário que está realizando a compra.
    // Essencial para rastreamento e para o fluxo de pagamento.
    private Long userId;
    
    // Lista dos itens que compõem o pedido.
    // Cada item deve ser um OrderItemRequest (que contém o productId e a quantidade).
    private List<OrderItemRequest> items;
}