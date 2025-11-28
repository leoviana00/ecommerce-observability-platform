package io.viana.order_service.exception;

/**
 * Exceção customizada (unchecked) lançada quando uma busca por ID
 * ou uma operação é realizada em um pedido cujo registro
 * não existe no banco de dados do 'order-service'.
 *
 * Estender RuntimeException significa que é uma exceção "unchecked",
 * o que simplifica o código do serviço, pois não exige o tratamento
 * explícito no 'throws' da assinatura do método.
 */
public class OrderNotFoundException extends RuntimeException {

    /**
     * Construtor que recebe o ID do pedido para criar uma mensagem de erro específica.
     *
     * @param orderId O ID do pedido que não foi encontrado.
     */
    public OrderNotFoundException(Long orderId) {
        // Chama o construtor da classe pai (RuntimeException) com a mensagem detalhada.
        super("Pedido não encontrado no sistema: " + orderId);
    }
}