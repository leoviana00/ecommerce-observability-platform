package io.viana.product_service.service;

import io.viana.product_service.dto.ProductDTO;
import io.viana.product_service.model.ProductEntity;
import io.viana.product_service.producer.ProductEventProducer;
import io.viana.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Camada de Serviço (Business Logic) para Produto.
 * Coordena persistência e publicação de eventos para outros serviços.
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final ProductEventProducer producer;

    /**
     * Cria um produto e dispara evento para o Inventory Service inicializar o estoque.
     */
    @Transactional
    public ProductEntity createProduct(ProductDTO dto) {

        // 1. Salva produto (sem estoque – responsabilidade do Inventory)
        ProductEntity product = ProductEntity.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .build();

        ProductEntity saved = repository.save(product);

        // 2. Publica evento para o Inventory iniciar estoque
        producer.sendProductCreated(saved, dto.initialStock());

        return saved;
    }

    // ----- Métodos de consulta -----

    public List<ProductEntity> findAll() {
        return repository.findAll();
    }

    public Optional<ProductEntity> findById(Long id) {
        return repository.findById(id);
    }

    public List<ProductEntity> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Consulta de produtos disponíveis NÃO é responsabilidade
     * deste serviço no modelo de microserviços.
     */
    public List<ProductEntity> findAvailableProducts() {
        return List.of(); // Placeholder correto
    }
}
