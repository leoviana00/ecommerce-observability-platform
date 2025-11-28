package io.viana.cart_service.exception;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException(Long userId) {
        super("Carrinho não encontrado para o usuário: " + userId);
    }
}
