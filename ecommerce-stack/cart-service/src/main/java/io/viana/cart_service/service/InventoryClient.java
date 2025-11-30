package io.viana.cart_service.service;

import io.viana.cart_service.dto.InventoryResponse;
import io.viana.cart_service.exception.ProductNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class InventoryClient {

    private final RestTemplate rest = new RestTemplate();

    // Agora lido via application.yml/application.properties
    @Value("${services.inventory}")
    private String inventoryBase;

    public InventoryResponse getInventory(Long productId) {
        try {
            String url = inventoryBase + "/inventory/" + productId;
            return rest.getForObject(url, InventoryResponse.class);

        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException(productId);
            }
            throw ex;

        } catch (Exception ex) {
            throw new RuntimeException(
                    "Erro ao consultar inventory-service: " + ex.getMessage(), ex);
        }
    }
}
