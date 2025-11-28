package io.viana.order_service.service;

import io.viana.order_service.dto.CreateOrderRequest;
import io.viana.order_service.dto.OrderItemRequest;
import io.viana.order_service.dto.OrderItemResponse;
import io.viana.order_service.dto.OrderResponse;
import io.viana.order_service.dto.events.OrderCreatedEvent;
import io.viana.order_service.exception.OrderNotFoundException;
import io.viana.order_service.model.OrderEntity;
import io.viana.order_service.model.OrderItemEntity;
import io.viana.order_service.producer.OrderEventProducer;
import io.viana.order_service.repository.OrderItemRepository;
import io.viana.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importante para controle transacional

import java.time.LocalDateTime;
import java.util.List;

/**
 * Serviço de Negócio: OrderService
 *
 * Responsável por:
 * 1. Processar a requisição de criação de novo pedido.
 * 2. Orquestrar a persistência do pedido e seus itens.
 * 3. Iniciar o fluxo de transação distribuída (SAGA) publicando o OrderCreatedEvent.
 * 4. Mapear entidades internas (Entity) para DTOs de resposta (Response).
 */
@Service
@RequiredArgsConstructor // Injeta o OrderRepository, OrderItemRepository e OrderEventProducer
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final OrderEventProducer orderProducer;

    /**
     * Processa a criação de um novo pedido e inicia o fluxo SAGA.
     *
     * @param request O DTO de entrada do cliente (CreateOrderRequest).
     * @return O DTO de resposta do pedido criado (OrderResponse).
     */
    @Transactional // Garante que a criação do pedido e dos itens seja atômica
    public OrderResponse createOrder(CreateOrderRequest request) {

        // ======== VALIDAÇÕES DO PEDIDO (Controles de Entrada) ========
        if (request == null) {
            throw new IllegalArgumentException("Requisição inválida");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("userId é obrigatório");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos 1 item");
        }

        // 1. Criar pedido base no estado inicial (PENDING/CREATED)
        OrderEntity order = OrderEntity.builder()
                .userId(request.getUserId())
                .createdAt(LocalDateTime.now())
                .status("CREATED") // Status inicial para iniciar o SAGA
                .total(0.0) // Total temporário
                .build();

        // Salva para obter o ID gerado pelo banco de dados
        orderRepo.save(order);

        double total = 0.0;

        // 2. Criar e persistir os itens do pedido
        for (OrderItemRequest itemRequest : request.getItems()) {

            // Validação do item
            if (itemRequest.getProductId() == null) {
                throw new IllegalArgumentException("productId é obrigatório para cada item");
            }
            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("quantity deve ser maior que zero");
            }

            // Futuro: Chamada REST ou busca de cache para obter o preço real do product-service.
            double unitPrice = 1.0; // <<< Valor mockado (FIXME)
            double itemTotal = itemRequest.getQuantity() * unitPrice;

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .orderId(order.getId()) // Usa o ID do pedido principal
                    .productId(itemRequest.getProductId())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .build();

            itemRepo.save(orderItem);
            total += itemTotal;
        }

        // 3. Atualiza o total final do pedido no banco de dados
        order.setTotal(total);
        orderRepo.save(order); // Atualiza o registro

        // ========================================
        // 4. PUBLICAR EVENTO (INÍCIO DO SAGA)
        // ========================================
        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .total(order.getTotal())
                .timestamp(System.currentTimeMillis())
                .build();

        // Envia o evento para o Kafka, iniciando a reserva de estoque e o pagamento
        // 
        orderProducer.sendOrderCreated(event);

        // Retorna a resposta completa do pedido recém-criado
        return getOrder(order.getId());
    }

    /**
     * Busca um pedido existente e o mapeia para o DTO de resposta.
     *
     * @param orderId ID do pedido a ser consultado.
     * @return O DTO OrderResponse com todos os detalhes.
     */
    @Transactional(readOnly = true) // Otimiza a transação apenas para leitura
    public OrderResponse getOrder(Long orderId) {

        // 1. Busca a entidade principal do pedido, lançando 404 se não for encontrada
        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        // 2. Busca todos os itens associados ao pedido
        List<OrderItemEntity> items = itemRepo.findByOrderId