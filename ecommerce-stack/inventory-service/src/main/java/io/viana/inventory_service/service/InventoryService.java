package io.viana.inventory_service.service;

import io.viana.inventory_service.dto.InventoryUpdatedEvent;
import io.viana.inventory_service.dto.InventoryReserveFailedEvent;
import io.viana.inventory_service.dto.InventoryResponse;
import io.viana.inventory_service.dto.ProductCreatedEvent;
import io.viana.inventory_service.exception.ProductNotFoundException;
import io.viana.inventory_service.exception.InsufficientStockException;
import io.viana.inventory_service.model.InventoryEntity;
import io.viana.inventory_service.producer.InventoryEventProducer;
import io.viana.inventory_service.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para controle transacional

/**
 * Serviço de Negócio: InventoryService
 *
 * Responsável por toda a lógica de negócio do inventário:
 * 1. Inicializar estoque (reagindo a eventos Kafka).
 * 2. Consultar disponibilidade de estoque (via REST).
 * 3. Manipular o estoque (aumento/diminuição), incluindo validação e emissão de eventos.
 */
@Service
@RequiredArgsConstructor // Injeta o Repository e o Producer via construtor
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository repo;
    private final InventoryEventProducer producer;

    /**
     * Lógica de Consumo de Evento: Cria o estoque inicial quando um produto é criado.
     *
     * @param event O evento ProductCreatedEvent recebido via Kafka.
     */
    @Transactional // Garante que a operação de persistência seja atômica
    public void createInitialStock(ProductCreatedEvent event) {

        // Prevenção de Duplicidade: Verifica se o registro já existe (Idempotência).
        boolean exists = repo.existsByProductId(event.getId());

        if (exists) {
            log.warn("Estoque já existe para o produto {}. Ignorando evento de criação.", event.getId());
            return;
        }

        // Mapeia o DTO do evento para a Entidade JPA
        InventoryEntity entity = InventoryEntity.builder()
                .productId(event.getId())
                .stockQuantity(event.getInitialStock() != null ? event.getInitialStock() : 0)
                .build();

        repo.save(entity);

        log.info("Estoque inicial criado | productId={} | qtd={}",
                event.getId(), entity.getStockQuantity());
    }

    /**
     * Lógica de Consulta: Retorna a disponibilidade atual do produto.
     *
     * @param productId ID do produto a ser consultado (geralmente por Order ou Cart Service).
     * @return DTO de resposta com a quantidade e o status de disponibilidade.
     */
    @Transactional(readOnly = true) // Otimiza a transação apenas para leitura
    public InventoryResponse getInventoryResponse(Long productId) {
        // Busca o registro e lança 404 caso não exista
        InventoryEntity entity = repo.findTopByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Integer quantity = entity.getStockQuantity() != null ? entity.getStockQuantity() : 0;

        return InventoryResponse.builder()
                .productId(entity.getProductId())
                .stockQuantity(quantity)
                .isAvailable(quantity > 0)
                .build();
    }

    /**
     * Lógica de Movimentação: Aumenta o estoque (ex: reposição, devolução).
     *
     * @param productId ID do produto.
     * @param amount Quantidade a ser adicionada.
     * @param reason Razão de negócio (RESTOCK, ADJUST, etc.).
     */
    @Transactional
    public void increaseStock(Long productId, Integer amount, String reason) {

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Quantidade inválida para aumento de estoque");
        }

        InventoryEntity entity = repo.findTopByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        int oldQty = entity.getStockQuantity();
        int newQty = oldQty + amount;

        // 1. Atualiza e salva a entidade no DB (transação)
        entity.setStockQuantity(newQty);
        repo.save(entity);

        log.info("Estoque aumentado | productId={} | oldQty={} | newQty={} | reason={}",
                productId, oldQty, newQty, reason);

        // 2. PUBLICA EVENTO Kafka (Notificação de sucesso)
        producer.sendInventoryUpdated(
                InventoryUpdatedEvent.builder()
                        .productId(productId)
                        .oldQuantity(oldQty)
                        .newQuantity(newQty)
                        .movementType("INCREASE")
                        .reason(reason)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }

    /**
     * Lógica de Movimentação: Reduz o estoque (reserva para pedido).
     * Implementa o fluxo de compensação SAGA em caso de falha. 
     *
     * @param productId ID do produto.
     * @param amount Quantidade a ser reservada/reduzida.
     * @param reason Razão de negócio (ORDER, etc.).
     */
    @Transactional
    public void decreaseStock(Long productId, Integer amount, String reason) {

        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Quantidade inválida para redução de estoque");
        }

        InventoryEntity entity = repo.findTopByProductId(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        int oldQty = entity.getStockQuantity();

        // 1. VALIDAÇÃO DE ESTOQUE (Ponto Crítico)
        if (oldQty < amount) {

            // Falha: PUBLICA EVENTO de falha ANTES de lançar a exceção.
            // O evento notifica o serviço de pedidos que ele precisa reverter (compensar).
            producer.sendReserveFailed(
                    InventoryReserveFailedEvent.builder()
                            .productId(productId)
                            .requested(amount)
                            .available(oldQty)
                            .reason(reason)
                            .timestamp(System.currentTimeMillis())
                            .build()
            );

            // Lança a exceção, que será capturada pelo @RestControllerAdvice e virará um 409 CONFLICT.
            throw new InsufficientStockException(productId, oldQty, amount);
        }

        // 2. MOVIMENTAÇÃO OK
        int newQty = oldQty - amount;

        entity.setStockQuantity(newQty);
        repo.save(entity); // Persiste a mudança de estado

        log.info("Estoque reduzido | productId={} | oldQty={} | newQty={} | reason={}",
                productId, oldQty, newQty, reason);

        // 3. PUBLICA EVENTO Kafka (Notificação de sucesso)
        producer.sendInventoryUpdated(
                InventoryUpdatedEvent.builder()
                        .productId(productId)
                        .oldQuantity(oldQty)
                        .newQuantity(newQty)
                        .movementType("DECREASE")
                        .reason(reason)
                        .timestamp(System.currentTimeMillis())
                        .build()
        );
    }
}