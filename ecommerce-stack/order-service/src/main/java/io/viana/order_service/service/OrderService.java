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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderItemRepository itemRepo;
    private final OrderEventProducer orderProducer;

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Requisição inválida");
        }
        if (request.getUserId() == null) {
            throw new IllegalArgumentException("userId é obrigatório");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos 1 item");
        }

        OrderEntity order = OrderEntity.builder()
                .userId(request.getUserId())
                .createdAt(LocalDateTime.now())
                .status("CREATED")
                .total(0.0)
                .build();

        orderRepo.save(order);

        double total = 0.0;

        for (OrderItemRequest itemRequest : request.getItems()) {

            if (itemRequest.getProductId() == null) {
                throw new IllegalArgumentException("productId é obrigatório");
            }
            if (itemRequest.getQuantity() == null || itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("quantity deve ser maior que zero");
            }

            double unitPrice = 1.0;
            double itemTotal = itemRequest.getQuantity() * unitPrice;

            OrderItemEntity orderItem = OrderItemEntity.builder()
                    .orderId(order.getId())
                    .productId(itemRequest.getProductId())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .build();

            itemRepo.save(orderItem);
            total += itemTotal;
        }

        order.setTotal(total);
        orderRepo.save(order);

        OrderCreatedEvent event = OrderCreatedEvent.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .total(order.getTotal())
                .timestamp(System.currentTimeMillis())
                .build();

        orderProducer.sendOrderCreated(event);

        return getOrder(order.getId());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long orderId) {

        OrderEntity order = orderRepo.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        List<OrderItemEntity> items = itemRepo.findByOrderId(order.getId());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(it -> OrderItemResponse.builder()
                        .productId(it.getProductId())
                        .quantity(it.getQuantity())
                        .unitPrice(it.getUnitPrice())
                        .total(it.getQuantity() * it.getUnitPrice())
                        .build()
                ).toList();

        double total = itemResponses.stream()
                .mapToDouble(OrderItemResponse::getTotal)
                .sum();

        return OrderResponse.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .items(itemResponses)
                .total(total)
                .status(order.getStatus())
                .build();
    }
}
