package io.viana.inventory_service.dto;

import lombok.Data; // Gera automaticamente Getters, Setters, toString(), equals() e hashCode()

/**
 * DTO (Data Transfer Object) usado para receber dados de entrada
 * (payload) em endpoints de movimentação de estoque (aumento ou diminuição).
 */
@Data
public class MovementRequest {

    // Quantidade (volume) que deve ser adicionada ou subtraída do estoque.
    private Integer amount;

    // Razão de negócio para a movimentação de estoque.
    // Essencial para auditoria e contexto.
    // Ex: ORDER (venda), RESTOCK (reposição), ADJUST (ajuste manual).
    private String reason;
}