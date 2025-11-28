package io.viana.order_service.exception;

import org.springframework.http.HttpStatus; // Enum para códigos de status HTTP
import org.springframework.http.ResponseEntity; // Classe para construir a resposta HTTP
import org.springframework.web.bind.annotation.*; // Anotações REST

/**
 * Define a classe como um manipulador de exceções global para todos os Controllers.
 * (@RestControllerAdvice combina @ControllerAdvice e @ResponseBody).
 * Ele captura exceções lançadas pela camada de Controller/Service e as mapeia
 * para respostas HTTP apropriadas e consistentes.
 */
@RestControllerAdvice
public class OrderExceptionHandler {

    /**
     * Manipula a exceção OrderNotFoundException (Pedido não encontrado).
     *
     * @param ex A exceção OrderNotFoundException lançada.
     * @return Resposta HTTP 404 Not Found com a mensagem da exceção.
     */
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<String> handleNotFound(OrderNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND) // Código 404
                .body(ex.getMessage());
    }

    /**
     * Manipula a exceção IllegalArgumentException (Argumentos inválidos).
     *
     * @param ex A exceção IllegalArgumentException lançada (ex: validação de entrada).
     * @return Resposta HTTP 400 Bad Request com a mensagem da exceção.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST) // Código 400
                .body(ex.getMessage());
    }

    /**
     * Manipula exceções genéricas (Catch-all para erros inesperados).
     *
     * @param ex A exceção genérica lançada.
     * @return Resposta HTTP 500 Internal Server Error, expondo a mensagem do erro,
     * mas evitando vazamento de stacktrace completo para o cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        // Log simples no console (substitua por logger se preferir)
        ex.printStackTrace(); // É importante logar o stacktrace para debugar erros 500.
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // Código 500
                .body("Erro interno: " + ex.getMessage());
    }
}