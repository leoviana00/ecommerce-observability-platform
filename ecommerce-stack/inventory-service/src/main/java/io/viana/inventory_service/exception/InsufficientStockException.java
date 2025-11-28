package io.viana.inventory_service.exception;

/**
 * Exceção customizada (unchecked) lançada quando uma operação tenta
 * diminuir o estoque de um produto, mas a quantidade solicitada
 * excede a quantidade disponível.
 *
 * Estender RuntimeException significa que esta é uma exceção "unchecked",
 * o que significa que ela não precisa ser declarada no 'throws' da assinatura
 * do método, simplificando o código do serviço.
 */
public class InsufficientStockException extends RuntimeException {

    /**
     * Construtor que recebe os detalhes da falha para construir uma mensagem informativa.
     *
     * @param productId O ID do produto que causou o erro.
     * @param available A quantidade de estoque atualmente disponível.
     * @param requested A quantidade solicitada na operação.
     */
    public InsufficientStockException(Long productId, int available, int requested) {
        // Chama o construtor da classe pai (RuntimeException) com a mensagem detalhada
        super(
            "Estoque insuficiente para o produto " + productId +
            " (Disponível: " + available + ", Requisitado: " + requested + ")"
        );
    }
}