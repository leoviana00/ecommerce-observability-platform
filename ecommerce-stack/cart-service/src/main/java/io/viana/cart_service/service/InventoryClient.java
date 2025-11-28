package io.viana.cart_service.service;

import io.viana.cart_service.dto.InventoryResponse;
import io.viana.cart_service.exception.ProductNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class InventoryClient {

    private final RestTemplate rest = new RestTemplate();
    private final String inventoryBase = "http://localhost:8080/inventory/";

    public InventoryResponse getInventory(Long productId) {
        try {
            return rest.getForObject(inventoryBase + productId, InventoryResponse.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ProductNotFoundException(productId);
            }
            // Re-throw as runtime with message (so handler returns 500)
            throw ex;
        } catch (Exception ex) {
            // Fail-fast with clear message
            throw new RuntimeException("Erro ao consultar inventory-service: " + ex.getMessage(), ex);
        }
    }
}
