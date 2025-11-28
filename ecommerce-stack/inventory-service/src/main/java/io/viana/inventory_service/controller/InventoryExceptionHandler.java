package io.viana.inventory_service.controller;

import io.viana.inventory_service.exception.ProductNotFoundException; // Exceção personalizada para Produto não encontrado
import io.viana.inventory_service.exception.InsufficientStockException; // Exceção personalizada para Estoque insuficiente

import org.springframework.http.HttpStatus; // Enum de códigos de status HTTP
import org.springframework.http.ResponseEntity; // Classe para construir a resposta HTTP
import org.springframework.web.bind.annotation.*; // Anotações REST

/**
 * Define a classe como um manipulador de exceções global para todos os Controllers.
 * O Spring a escuta para capturar exceções lançadas por qualquer método @RequestMapping.
 */
@RestControllerAdvice
public class InventoryExceptionHandler {

    /**
     * Manipula a exceção ProductNotFoundException (Produto não encontrado no estoque).
     * @param ex A exceção ProductNotFoundException lançada.
     * @return Resposta HTTP 404 Not Found com a mensagem da exceção.
     */
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<String> handleProductNotFound(ProductNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND) // Código 404
                .body(ex.getMessage());
    }

    /**
     * Manipula a exceção InsufficientStockException (Tentativa de reduzir estoque abaixo de zero).
     * @param ex A exceção InsufficientStockException lançada.
     * @return Resposta HTTP 409 Conflict com a mensagem da exceção.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStock(InsufficientStockException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // Código 409
                .body(ex.getMessage());
    }

    /**
     * Manipula a exceção IllegalArgumentException (Erros de validação básicos, ex: parâmetro inválido).
     * @param ex A exceção IllegalArgumentException lançada.
     * @return Resposta HTTP 400 Bad Request com a mensagem da exceção.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleInvalidArgument(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // Código 400
                .body(ex.getMessage());
    }

    /**
     * Manipula exceções genéricas (Catch-all para erros inesperados).
     * É a última linha de defesa para erros não mapeados.
     *
     * @param ex A exceção genérica lançada.
     * @return Resposta HTTP 500 Internal Server Error, evitando expor detalhes internos.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericError(Exception ex) {
        // Logar o 'ex' aqui é essencial para o monitoramento!
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR) // Código 500
                .body("Erro interno ao processar a requisição.");
    }
}