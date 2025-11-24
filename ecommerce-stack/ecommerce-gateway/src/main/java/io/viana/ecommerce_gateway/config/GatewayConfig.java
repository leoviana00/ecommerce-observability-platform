package io.viana.ecommerce_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Marca esta classe como uma fonte de definição de beans e configurações
@Configuration
public class GatewayConfig {

    // Injeta o valor da URL do serviço de produto a partir das propriedades
    @Value("${services.product}")
    private String productUrl;

    // Injeta o valor da URL do serviço de inventário a partir das propriedades
    @Value("${services.inventory}")
    private String inventoryUrl;

    // Injeta o valor da URL do serviço de carrinho a partir das propriedades
    @Value("${services.cart}")
    private String cartUrl;

    // Injeta o valor da URL do serviço de pedido a partir das propriedades
    @Value("${services.order}")
    private String orderUrl;

    // Injeta o valor da URL do serviço de pagamento a partir das propriedades
    @Value("${services.payment}")
    private String paymentUrl;

    // Injeta o valor da URL do serviço de notificação a partir das propriedades
    @Value("${services.notification}")
    private String notificationUrl;

    // Define um bean para configurar as rotas do Spring Cloud Gateway
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        // Inicia a construção das rotas
        return builder.routes()

            // --- ROTA DO SERVIÇO DE PRODUTO ---
            .route("product-service", r -> r
                // Define que a rota será acionada por caminhos que começam com /products/
                .path("/products/**")
                // Aplica filtros a esta rota
                .filters(f -> f
                    // Tenta a requisição 3 vezes em caso de falha
                    .retry(3)
                    // Configura um Circuit Breaker (Disjuntor) para tratamento de falhas
                    .circuitBreaker(c -> c
                        // Nome do Circuit Breaker
                        .setName("productCircuit")
                        // URI de fallback (retorno) em caso de circuito aberto/erro
                        .setFallbackUri("forward:/fallback/products")))
                // Encaminha a requisição para a URL do serviço de produto
                .uri(productUrl))

            // --- ROTA DO SERVIÇO DE INVENTÁRIO ---
            .route("inventory-service", r -> r
                // Define que a rota será acionada por caminhos que começam com /inventory/
                .path("/inventory/**")
                // Aplica filtros a esta rota
                .filters(f -> f
                    // Configura um Circuit Breaker
                    .circuitBreaker(c -> c
                        .setName("inventoryCircuit")
                        .setFallbackUri("forward:/fallback/inventory")))
                // Encaminha a requisição para a URL do serviço de inventário
                .uri(inventoryUrl))

            // --- ROTA DO SERVIÇO DE CARRINHO ---
            .route("cart-service", r -> r
                // Define que a rota será acionada por caminhos que começam com /cart/
                .path("/cart/**")
                // Aplica filtros a esta rota
                .filters(f -> f.retry(2)) // Tenta a requisição 2 vezes em caso de falha
                // Encaminha a requisição para a URL do serviço de carrinho
                .uri(cartUrl))

            // --- ROTA DO SERVIÇO DE PEDIDO ---
            .route("order-service", r -> r
                // Define que a rota será acionada por caminhos que começam com /orders/
                .path("/orders/**")
                // Aplica filtros a esta rota
                .filters(f -> f.retry(2)) // Tenta a requisição 2 vezes em caso de falha
                // Encaminha a requisição para a URL do serviço de pedido
                .uri(orderUrl))

            // --- ROTA DO SERVIÇO DE PAGAMENTO ---
            .route("payment-service", r -> r
                // Define que a rota será acionada por caminhos que começam com /payments/
                .path("/payments/**")
                // Aplica filtros a esta rota
                .filters(f -> f.circuitBreaker(c -> c // Configura um Circuit Breaker
                        .setName("paymentCircuit")
                        .setFallbackUri("forward:/fallback/payment")))
                // Encaminha a requisição para a URL do serviço de pagamento
                .uri(paymentUrl))

            // --- ROTA DO SERVIÇO DE NOTIFICAÇÃO ---
            .route("notification-service", r -> r
                // Define que a rota será acionada por caminhos que começam com /notifications/
                .path("/notifications/**")
                // Encaminha a requisição para a URL do serviço de notificação
                .uri(notificationUrl))

            // Conclui a construção das rotas
            .build();
    }
}