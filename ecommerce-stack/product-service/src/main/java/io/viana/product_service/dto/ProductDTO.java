package io.viana.product_service.dto;

/**
 * ProductDTO (Java Record)
 *
 * Um Data Transfer Object (DTO) usado para receber dados de entrada
 * (Payload) ao criar um novo produto.
 *
 * Utiliza o recurso 'record' do Java (a partir do Java 16),
 * que é ideal para classes que apenas transportam dados.
 */
public record ProductDTO(
        // Nome do produto (Campo obrigatório para catálogo)
        String name,

        // Descrição do produto (Pode ser opcional dependendo da regra de negócio)
        String description,

        // Preço unitário do produto (Campo obrigatório para catálogo)
        Double price,

        // Estoque inicial.
        // É o valor que será enviado em um evento para o 'inventory-service'
        // após a criação bem-sucedida do produto no 'product-service'.
        Integer initialStock
) {}