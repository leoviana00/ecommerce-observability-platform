package io.viana.ecommerce_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal-fallback")
public class FallbackController {

    @RequestMapping("/products")
    public ResponseEntity<String> productServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Produtos indisponível no momento. Tente novamente mais tarde.");
    }

    @RequestMapping("/inventory")
    public ResponseEntity<String> inventoryServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Estoque indisponível no momento.");
    }

    @RequestMapping("/cart")
    public ResponseEntity<String> cartServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Carrinho indisponível no momento.");
    }

    @RequestMapping("/order")
    public ResponseEntity<String> orderServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Pedidos indisponível no momento.");
    }

    @RequestMapping("/payment")
    public ResponseEntity<String> paymentServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Pagamento indisponível no momento.");
    }

    @RequestMapping("/notification")
    public ResponseEntity<String> notificationServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Notificação indisponível no momento.");
    }
}
