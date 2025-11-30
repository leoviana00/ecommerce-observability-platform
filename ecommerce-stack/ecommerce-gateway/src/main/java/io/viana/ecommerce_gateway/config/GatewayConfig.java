package io.viana.ecommerce_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${services.product}") private String productUrl;
    @Value("${services.inventory}") private String inventoryUrl;
    @Value("${services.cart}") private String cartUrl;
    @Value("${services.order}") private String orderUrl;
    @Value("${services.payment}") private String paymentUrl;
    @Value("${services.notification}") private String notificationUrl;

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {

        System.out.println("========== GATEWAY CONFIG DEBUG ==========");
        System.out.println("Product URL:      " + productUrl);
        System.out.println("Inventory URL:    " + inventoryUrl);
        System.out.println("Cart URL:         " + cartUrl);
        System.out.println("Order URL:        " + orderUrl);
        System.out.println("Payment URL:      " + paymentUrl);
        System.out.println("Notification URL: " + notificationUrl);
        System.out.println("==========================================");

        return builder.routes()

            // PRODUCT
            .route("product-service", r -> r
                .path("/products/**")
                .filters(f -> f.retry(3))
                .uri(productUrl))

            // INVENTORY (sem fallback!)
            .route("inventory-service", r -> r
                .path("/inventory/**")
                .filters(f -> f.retry(1))
                .uri(inventoryUrl))

            // CART
            .route("cart-service", r -> r
                .path("/cart/**")
                .filters(f -> f.retry(2))
                .uri(cartUrl))

            // ORDER
            .route("order-service", r -> r
                .path("/orders/**")
                .filters(f -> f.retry(2))
                .uri(orderUrl))

            // PAYMENT
            .route("payment-service", r -> r
                .path("/payments/**")
                .filters(f -> f
                    .retry(2)
                    .circuitBreaker(c -> c
                        .setName("paymentCircuit")
                        .setFallbackUri("forward:/internal-fallback/payment")))
                .uri(paymentUrl))

            // NOTIFICATION
            .route("notification-service", r -> r
                .path("/notifications/**")
                .uri(notificationUrl))

            .build();
    }
}
