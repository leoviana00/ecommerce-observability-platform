package io.viana.ecommerce_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Marca a classe como um controlador REST, capaz de lidar com requisições HTTP
@RestController
public class FallbackController {

    // --- Rotas de Fallback ---
    // Essas rotas são mapeadas no GatewayConfig via .setFallbackUri("forward:/fallback/...")

    // Fallback para o Serviço de Produtos
    @GetMapping("/fallback/products")
    public ResponseEntity<String> productServiceFallback() {
        return ResponseEntity
                // Define o status HTTP 503 (Service Unavailable)
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                // Retorna uma mensagem informativa no corpo da resposta
                .body("Serviço de Produtos indisponível no momento. Tente novamente mais tarde.");
    }

    // Fallback para o Serviço de Inventário (Estoque)
    @GetMapping("/fallback/inventory")
    public ResponseEntity<String> inventoryServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Estoque indisponível no momento. Não é possível verificar a disponibilidade.");
    }

    // Fallback para o Serviço de Carrinho
    @GetMapping("/fallback/cart")
    public ResponseEntity<String> cartServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Carrinho indisponível no momento.");
    }

    // Fallback para o Serviço de Pedidos
    @GetMapping("/fallback/order")
    public ResponseEntity<String> orderServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Pedidos indisponível no momento. Não é possível finalizar a compra.");
    }

    // Fallback para o Serviço de Pagamento
    @GetMapping("/fallback/payment")
    public ResponseEntity<String> paymentServiceFallback() {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Serviço de Pagamento indisponível no momento.");
    }
}