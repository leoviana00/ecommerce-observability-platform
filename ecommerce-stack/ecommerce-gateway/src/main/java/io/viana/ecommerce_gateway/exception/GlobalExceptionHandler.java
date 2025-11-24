package io.viana.ecommerce_gateway.exception;

import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

// Combina @ControllerAdvice e @ResponseBody. 
// Isso permite que a classe trate exceções lançadas por qualquer @Controller no aplicativo.
@RestControllerAdvice 
public class GlobalExceptionHandler {

    /**
     * Captura qualquer exceção não especificada (Exception.class) lançada em todo o aplicativo.
     * * @param ex A exceção que foi lançada.
     * @return Uma resposta padronizada com status 500 (Internal Server Error).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handle(Exception ex) {
        // Mapa para construir o corpo da resposta JSON
        Map<String, String> body = new HashMap<>();
        
        // Adiciona o nome simples da classe da exceção (ex: NullPointerException)
        body.put("error", ex.getClass().getSimpleName());
        
        // Adiciona a mensagem detalhada da exceção
        body.put("message", ex.getMessage());
        
        // Tenta obter e adicionar o ID de Correlação do MDC (Mapped Diagnostic Context).
        // Isso é crucial para rastreamento distribuído e depuração.
        body.put("correlationId", MDC.get("correlationId"));

        // Retorna a resposta HTTP:
        // - Status: 500 Internal Server Error (Erro Interno do Servidor)
        // - Corpo: O mapa contendo os detalhes do erro e o correlationId
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}