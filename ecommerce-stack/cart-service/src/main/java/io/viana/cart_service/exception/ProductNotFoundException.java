package io.viana.cart_service.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super("Produto não encontrado: " + productId);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
