package io.viana.product_service.dto;

import lombok.*;

/**
 * ProductCreatedEvent
 *
 * DTO (Data Transfer Object) que representa um **evento** disparado
 * após um produto ser criado com sucesso no 'product-service'.
 *
 * É usado para comunicação assíncrona entre serviços (ex: via Kafka).
 */
@Data // Gera Getters, Setters, toString(), equals() e hashCode().
@Builder // Permite a criação de objetos de forma fluente (ProductCreatedEvent.builder()...).
@NoArgsConstructor // Construtor padrão sem argumentos (necessário para serialização).
@AllArgsConstructor // Construtor com todos os campos.
public class ProductCreatedEvent {

    // ID do produto recém-criado. Essencial para identificar o recurso.
    private Long id;

    // Nome do produto. Útil para logging ou contexto no serviço consumidor.
    private String name;

    // Estoque Inicial.
    // Este campo é crucial. Ele informa ao 'inventory-service' qual é a
    // quantidade inicial que deve ser registrada no seu banco de dados
    // após receber este evento.
    private Integer initialStock;
}