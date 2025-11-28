package io.viana.cart_service.dto;

import lombok.Data;

@Data
public class InventoryResponse {
    private Long productId;
    private Integer stockQuantity;
    private Boolean isAvailable;
}
