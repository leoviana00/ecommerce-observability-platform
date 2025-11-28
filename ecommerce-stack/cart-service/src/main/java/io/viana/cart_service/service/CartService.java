package io.viana.cart_service.service;

import io.viana.cart_service.dto.*;
import io.viana.cart_service.exception.ProductUnavailableException;
import io.viana.cart_service.exception.CartNotFoundException;
import io.viana.cart_service.exception.ProductNotFoundException;
import io.viana.cart_service.model.CartEntity;
import io.viana.cart_service.model.CartItemEntity;
import io.viana.cart_service.repository.CartItemRepository;
import io.viana.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepo;
    private final CartItemRepository itemRepo;
    private final InventoryClient inventoryClient;

    /**
     * Retorna o carrinho do usuário ou cria um novo.
     */
    @Transactional
    public CartEntity getOrCreateCart(Long userId) {
        return cartRepo.findByUserId(userId)
                .orElseGet(() -> cartRepo.save(
                        CartEntity.builder()
                                .userId(userId)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ));
    }

    /**
     * Adiciona item ao carrinho.
     * Valida:
     * - request nulo ou inválido
     * - produto inexistente (inventory 404 → ProductNotFoundException)
     * - estoque insuficiente
     */
    @Transactional
    public CartResponse addItem(Long userId, AddItemRequest request) {

        // ------- 1. Validação básica -------
        if (request == null) {
            throw new IllegalArgumentException("Requisição inválida");
        }

        if (request.getProductId() == null) {
            throw new IllegalArgumentException("productId é obrigatório");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("quantity deve ser maior que zero");
        }

        // ------- 2. Buscar estoque no InventoryService -------
        InventoryResponse inv = null;
        try {
            inv = inventoryClient.getInventory(request.getProductId());
        } catch (ProductNotFoundException ex) {
            // Deixar a exception fluir para o handler
            throw ex;
        } catch (Exception ex) {
            // Problema de comunicação
            throw new RuntimeException("Falha ao consultar inventory-service: " + ex.getMessage(), ex);
        }

        if (inv == null) {
            throw new ProductNotFoundException(request.getProductId());
        }

        // ------- 3. Validar disponibilidade -------
        if (!inv.getIsAvailable() || inv.getStockQuantity() < request.getQuantity()) {
            throw new ProductUnavailableException(request.getProductId());
        }

        // ------- 4. Obter ou criar carrinho -------
        CartEntity cart = getOrCreateCart(userId);

        // ------- 5. Verificar existência de item -------
        var itemOpt = itemRepo.findByCartIdAndProductId(cart.getId(), request.getProductId());

        CartItemEntity item;

        if (itemOpt.isPresent()) {
            // Atualizar quantidade
            item = itemOpt.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
        } else {
            // Criar novo item
            item = CartItemEntity.builder()
                    .cartId(cart.getId())
                    .productId(request.getProductId())
                    .productName("Product " + request.getProductId()) // futuro: integração product-service
                    .unitPrice(1.0)                                  // futuro: preço real do catálogo
                    .quantity(request.getQuantity())
                    .build();
        }

        itemRepo.save(item);

        return buildCartResponse(cart);
    }

    /**
     * Consulta o carrinho do usuário.
     */
    @Transactional(readOnly = true)
    public CartResponse getCart(Long userId) {
        CartEntity cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        return buildCartResponse(cart);
    }

    /**
     * Remove item do carrinho.
     */
    @Transactional
    public void removeItem(Long userId, Long productId) {
        CartEntity cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        itemRepo.findByCartIdAndProductId(cart.getId(), productId)
                .ifPresent(itemRepo::delete);
    }

    /**
     * Monta o DTO de resposta para o cliente.
     */
    private CartResponse buildCartResponse(CartEntity cart) {
        List<CartItemEntity> items = itemRepo.findByCartId(cart.getId());

        var responseItems = items.stream().map(
                it -> CartItemResponse.builder()
                        .productId(it.getProductId())
                        .productName(it.getProductName())
                        .unitPrice(it.getUnitPrice())
                        .quantity(it.getQuantity())
                        .total(it.getQuantity() * it.getUnitPrice())
                        .build()
        ).toList();

        double total = responseItems.stream()
                .mapToDouble(CartItemResponse::getTotal)
                .sum();

        return CartResponse.builder()
                .userId(cart.getUserId())
                .items(responseItems)
                .total(total)
                .build();
    }
}
