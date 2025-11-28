package io.viana.inventory_service.dto;

import lombok.AllArgsConstructor; // Gera construtor com todos os campos
import lombok.Builder; // Permite a criação de objetos usando o padrão Builder
import lombok.Data; // Gera Getters, Setters, toString(), equals() e hashCode()
import lombok.NoArgsConstructor; // Gera construtor padrão sem argumentos

/**
 * Evento de Domínio: InventoryReserveFailedEvent
 *
 * Este DTO representa um **evento de falha** emitido pelo 'inventory-service'
 * quando uma tentativa de reserva ou diminuição de estoque falha
 * devido à **falta de quantidade disponível**.
 *
 * É crucial para a comunicação assíncrona, permitindo que o serviço
 * solicitante (ex: 'order-service') possa reverter ou compensar sua transação (SAGA).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReserveFailedEvent {

    // ID do produto que falhou na reserva.
    private Long productId;

    // Quantidade que foi solicitada para reserva/diminuição.
    private Integer requested;

    // Quantidade que estava realmente disponível no momento da falha.
    private Integer available;

    // Razão pela qual a reserva foi feita (ex: ID da Ordem, 'ORDER').
    // Ajuda o serviço consumidor a correlacionar a falha.
    private String reason;

    // Momento exato em que a falha ocorreu (útil para rastreamento e auditoria).
    private long timestamp;
}