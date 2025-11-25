package io.viana.product_service.service;

import io.viana.product_service.dto.ProductDTO;
import io.viana.product_service.model.ProductEntity;
import io.viana.product_service.producer.ProductEventProducer; // Importação do Produtor Kafka
import io.viana.product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importação para gerenciamento transacional

import java.util.List;
import java.util.Optional;

/**
 * Camada de Serviço (Business Logic) para a entidade Produto.
 * Contém a lógica de negócio e coordena a persistência de dados (DB) com a comunicação (Kafka).
 */
@Service
@RequiredArgsConstructor  // Gera construtor com todos os campos 'final' (DI via Construtor)
public class ProductService {

    // Injeção do Repositório (acesso ao DB) e do Produtor de Eventos (acesso ao Kafka)
    private final ProductRepository repository;
    private final ProductEventProducer producer;

    /**
     * Cria um novo produto no catálogo.
     * Deve ser uma operação atômica (DB save E Kafka publish).
     * @param dto DTO com os dados do novo produto.
     * @return A entidade ProductEntity salva.
     */
    @Transactional // Garante que a operação seja transacional (idealmente via Outbox Pattern para Kafka).
    public ProductEntity createProduct(ProductDTO dto) {
        // 1. Mapeamento DTO para Entity
        ProductEntity product = ProductEntity.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                // NOTA CRÍTICA: O campo 'stock' DEVE ser removido aqui
                // para que a responsabilidade seja do Inventory Service.
                // O DTO pode reter o stock inicial para ser enviado via evento.
                // .stock(dto.stock()) // REMOVER QUANDO AJUSTAR ENTITY
                .build();

        // 2. Persistência no Banco de Dados
        ProductEntity saved = repository.save(product);

        // 3. Publica evento assíncrono para notificar outros serviços (Inventory, Cart)
        // O evento leva o produto recém-criado (saved).
        producer.sendProductCreated(saved);

        return saved;
    }

    // --- Métodos de Leitura (Consulta) ---

    public List<ProductEntity> findAll() {
        return repository.findAll();
    }

    public Optional<ProductEntity> findById(Long id) {
        return repository.findById(id);
    }

    // Exemplo: busca por nome (utiliza o método customizado do repositório)
    public List<ProductEntity> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    /**
     * Exemplo: busca produtos com estoque disponível.
     * NOTA CRÍTICA DE ARQUITETURA: Este método é INCOMPATÍVEL com a separação de serviços.
     * A busca por disponibilidade deve:
     * 1. Listar todos os produtos (deste serviço).
     * 2. Consultar o 'inventory-service' (via REST, gRPC) para saber quais IDs têm estoque > 0.
     * 3. Filtrar a lista final.
     */
    public List<ProductEntity> findAvailableProducts() {
        // return repository.findByStockGreaterThan(0); // ESTE MÉTODO DEVE SER REMOVIDO
        // Lógica ideal: Chamar o Inventory Service.
        return List.of(); // Retorno mock (simulado) para o código corrigido.
    }
}