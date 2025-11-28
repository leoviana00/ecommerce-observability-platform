package io.viana.inventory_service.dto;

import lombok.AllArgsConstructor; // Gera construtor com todos os campos
import lombok.Builder; // Permite a criação de objetos usando o padrão Builder
import lombok.Data; // Gera Getters, Setters, toString(), equals() e hashCode()
import lombok.NoArgsConstructor; // Gera construtor padrão sem argumentos

/**
 * Evento de Domínio: InventoryUpdatedEvent
 *
 * Este DTO representa um **evento de sucesso** emitido pelo 'inventory-service'
 * toda vez que a quantidade de estoque de um produto é alterada.
 *
 * É vital para:
 * 1. **Auditoria:** Rastrear o estado do estoque ao longo do tempo.
 * 2. **Reatividade:** Permitir que outros serviços (ex: Notificação) reajam a mudanças.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdatedEvent {

    // ID do produto cujo estoque foi alterado.
    private Long productId;

    // Quantidade de estoque antes da movimentação.
    private Integer oldQuantity;

    // Quantidade de estoque após a movimentação.
    private Integer newQuantity;

    // Tipo de movimento ocorrido: 'INCREASE' ou 'DECREASE'.
    private String movementType;

    // Razão de negócio para a mudança (ex: 'ORDER' para venda, 'RESTOCK' para reposição).
    private String reason;

    // Momento exato em que a atualização de estoque ocorreu (útil para auditoria).
    private long timestamp;
}