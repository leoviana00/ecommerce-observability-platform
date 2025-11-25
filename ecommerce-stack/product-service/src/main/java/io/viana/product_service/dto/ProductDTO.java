package io.viana.product_service.dto;

/**
 * ProductDTO (Data Transfer Object)
 *
 * É um objeto de transferência de dados, usado para:
 * 1. Receber dados de entrada (payload) de requisições HTTP (ex: no método POST do Controller).
 * 2. Enviar dados de saída como resposta HTTP.
 *
 * O uso de DTOs desacopla a comunicação externa (API) dos modelos de banco de dados (ProductEntity).
 *
 * O 'record' do Java (disponível a partir do Java 16) é uma forma concisa de criar classes
 * de dados imutáveis (read-only), reduzindo a necessidade de código boilerplate (getters, equals, hashCode).
 */
public record ProductDTO(
        // Identificador do produto (pode ser opcional na criação, mas útil na atualização)
        Long id,
        // Nome do produto (Campo obrigatório)
        String name,
        // Descrição detalhada do produto
        String description,
        // Preço de venda do produto
        Double price,
        // Quantidade em estoque (Pode ser usado na criação, mas o controle fica, idealmente, no Inventory Service)
        Integer stock
) {}