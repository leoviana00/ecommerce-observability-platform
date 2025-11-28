package io.viana.cart_service.controller;

import io.viana.cart_service.dto.AddItemRequest;
import io.viana.cart_service.dto.CartResponse;
import io.viana.cart_service.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /**
     * Adiciona um item ao carrinho do usuário.
     * Validação ocorre via @Valid e é tratada pelo CartExceptionHandler.
     */
    @PostMapping("/{userId}/items")
    public CartResponse addItem(
            @PathVariable Long userId,
            @Valid @RequestBody AddItemRequest request
    ) {
        return cartService.addItem(userId, request);
    }

    /**
     * Retorna o carrinho do usuário.
     */
    @GetMapping("/{userId}")
    public CartResponse getCart(@PathVariable Long userId) {
        return cartService.getCart(userId);
    }

    /**
     * Remove um item do carrinho.
     */
    @DeleteMapping("/{userId}/items/{productId}")
    public void deleteItem(
            @PathVariable Long userId,
            @PathVariable Long productId
    ) {
        cartService.removeItem(userId, productId);
    }
}
