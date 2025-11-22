package io.viana.ecommerce_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller que lida com as respostas de fallback (circuit breaker aberto).
 * Retorna uma resposta amigável em caso de falha nos microsserviços.
 */
@RestController
public class FallbackController {

    @GetMapping("/fallback/products")
    public ResponseEntity<String> productServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Produtos indisponível no momento. Tente novamente mais tarde.");
    }

    @GetMapping("/fallback/inventory")
    public ResponseEntity<String> inventoryServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Estoque indisponível no momento. Não é possível verificar a disponibilidade.");
    }

    @GetMapping("/fallback/cart")
    public ResponseEntity<String> cartServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Carrinho indisponível no momento.");
    }

    @GetMapping("/fallback/order")
    public ResponseEntity<String> orderServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Pedidos indisponível no momento. Não é possível finalizar a compra.");
    }

    @GetMapping("/fallback/payment")
    public ResponseEntity<String> paymentServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Pagamento indisponível no momento.");
    }
}