package io.viana.cart_service.exception;

public class ProductUnavailableException extends RuntimeException {
    public ProductUnavailableException(Long productId) {
        super("Produto sem estoque disponível: " + productId);
    }
}
