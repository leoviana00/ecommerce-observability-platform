package io.viana.inventory_service.dto;

import lombok.*; // Importa anotações Lombok

/**
 * Evento de Domínio: ProductCreatedEvent
 *
 * Este DTO representa a **mensagem de entrada** esperada pelo 'inventory-service'.
 * Ele é consumido de um tópico Kafka (enviado pelo 'product-service')
 * para iniciar o registro de estoque para um novo produto.
 */
@Data // Gera Getters, Setters, toString(), equals() e hashCode().
@Builder // Permite a criação de objetos usando o padrão Builder.
@NoArgsConstructor // Construtor padrão (necessário para desserialização do Kafka/JSON).
@AllArgsConstructor // Construtor com todos os campos.
public class ProductCreatedEvent {
    
    // ID do produto criado (chave primária no 'product-service').
    // O inventory-service usará este ID para criar seu próprio registro de estoque.
    private Long id;
    
    // Nome do produto (útil para logs e contexto).
    private String name;
    
    // Quantidade inicial de estoque.
    // Este é o dado crucial que o inventory-service precisa para inicializar o estoque.
    private Integer initialStock;
}