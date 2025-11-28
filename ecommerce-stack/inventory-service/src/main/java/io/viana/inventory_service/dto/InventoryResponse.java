package io.viana.inventory_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para formatar a resposta da consulta de inventário.
 * Usado pelo Cart Service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryResponse {
    
    // ID do produto correspondente
    private Long productId;
    
    // Quantidade em estoque
    private Integer stockQuantity;
    
    // Flag de disponibilidade (estoque > 0)
    private Boolean isAvailable; 
}