package io.viana.inventory_service.model; // Ou event

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

/**
 * ProductEvent (DTO de Evento).
 *
 * Classe usada para desserializar o payload JSON do evento 'product-created'
 * enviado pelo product-service. Contém apenas dados de catálogo, conforme 
 * a responsabilidade do serviço de origem.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductEvent {

    // ID do produto (Chave do registro de estoque)
    private Long id; 

    // Dados de catálogo
    private String name;
    private String description;
    private Double price;
    
    // Campo 'stock' foi removido, pois o product-service não o envia.
}