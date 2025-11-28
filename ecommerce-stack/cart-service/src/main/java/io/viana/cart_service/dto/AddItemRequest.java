package io.viana.cart_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemRequest {

    @NotNull(message = "productId é obrigatório")
    private Long productId;

    @NotNull(message = "quantity é obrigatório")
    @Min(value = 1, message = "quantity deve ser >= 1")
    private Integer quantity;
}
