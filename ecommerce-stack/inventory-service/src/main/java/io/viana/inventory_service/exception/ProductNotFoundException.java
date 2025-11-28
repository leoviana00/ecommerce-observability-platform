package io.viana.inventory_service.exception;

/**
 * Exceção customizada (unchecked) lançada quando uma busca por ID
 * ou uma operação de estoque é realizada em um produto cujo registro
 * não existe no banco de dados do 'inventory-service'.
 *
 * Estender RuntimeException significa que é uma exceção "unchecked",
 * o que simplifica o código do serviço, pois não exige o tratamento
 * explícito no 'throws' da assinatura do método.
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Construtor que recebe o ID do produto para criar uma mensagem de erro específica.
     *
     * @param productId O ID do produto que não foi encontrado.
     */
    public ProductNotFoundException(Long productId) {
        // Chama o construtor da classe pai (RuntimeException) com a mensagem detalhada.
        super("Produto não encontrado no estoque: " + productId);
    }
}