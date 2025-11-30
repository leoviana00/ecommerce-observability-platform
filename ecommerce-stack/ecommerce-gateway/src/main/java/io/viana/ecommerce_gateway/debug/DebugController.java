package io.viana.ecommerce_gateway.debug;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class DebugController {

    @Value("${services.product:undefined}")
    private String productUrl;

    @Value("${services.inventory:undefined}")
    private String inventoryUrl;

    @Value("${services.cart:undefined}")
    private String cartUrl;

    @Value("${services.order:undefined}")
    private String orderUrl;

    @Value("${services.payment:undefined}")
    private String paymentUrl;

    @Value("${services.notification:undefined}")
    private String notificationUrl;

    @Value("${spring.kafka.bootstrap-servers:undefined}")
    private String kafkaBootstrap;

    @Value("${spring.profiles.active:undefined}")
    private String activeProfile;

    @GetMapping("/debug/config")
    public Map<String, Object> debugConfig() {
        return Map.of(
                "activeProfile", activeProfile,
                "kafkaBootstrapServers", kafkaBootstrap,
                "services", Map.of(
                        "product", productUrl,
                        "inventory", inventoryUrl,
                        "cart", cartUrl,
                        "order", orderUrl,
                        "payment", paymentUrl,
                        "notification", notificationUrl
                )
        );
    }
}
