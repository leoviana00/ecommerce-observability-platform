package io.viana.inventory_service.controller;

import io.viana.inventory_service.dto.MovementRequest;
import io.viana.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryMovementController {

    private final InventoryService service;

    @PostMapping("/{productId}/increase")
    @ResponseStatus(HttpStatus.OK)
    public void increaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody MovementRequest req
    ) {
        service.increaseStock(productId, req.getAmount(), req.getReason());
    }

    @PostMapping("/{productId}/decrease")
    @ResponseStatus(HttpStatus.OK)
    public void decreaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody MovementRequest req
    ) {
        service.decreaseStock(productId, req.getAmount(), req.getReason());
    }
}
