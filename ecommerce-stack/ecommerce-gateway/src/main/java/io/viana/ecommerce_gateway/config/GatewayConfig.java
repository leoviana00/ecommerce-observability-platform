package io.viana.ecommerce_gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()

            // PRODUCT SERVICE
            .route("product-service", r -> r
                .path("/products/**")
                .filters(f -> f
                    .retry(3)
                    .circuitBreaker(c -> c
                        .setName("productCircuit")
                        .setFallbackUri("forward:/fallback/product")))
                .uri("http://localhost:8081"))

            // INVENTORY SERVICE
            .route("inventory-service", r -> r
                .path("/inventory/**")
                .filters(f -> f
                    .circuitBreaker(c -> c
                        .setName("inventoryCircuit")
                        .setFallbackUri("forward:/fallback/inventory")))
                .uri("http://localhost:8082"))

            // CART SERVICE
            .route("cart-service", r -> r
                .path("/cart/**")
                .uri("http://localhost:8083"))

            // ORDER SERVICE
            .route("order-service", r -> r
                .path("/orders/**")
                .filters(f -> f.retry(2))
                .uri("http://localhost:8084"))

            // PAYMENT SERVICE
            .route("payment-service", r -> r
                .path("/payments/**")
                .filters(f -> f
                    .circuitBreaker(c -> c
                        .setName("paymentCircuit")
                        .setFallbackUri("forward:/fallback/payment")))
                .uri("http://localhost:8085"))

            // NOTIFICATION SERVICE
            .route("notification-service", r -> r
                .path("/notifications/**")
                .uri("http://localhost:8086"))

            .build();
    }
}
