package io.viana.cart_service.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CartItemResponse {
    private Long productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity;
    private Double total;
}
