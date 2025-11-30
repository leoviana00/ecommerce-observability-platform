package io.viana.inventory_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MovementRequest {

    @NotNull
    @Min(1)
    private Integer amount;

    @NotBlank
    private String reason;
}
