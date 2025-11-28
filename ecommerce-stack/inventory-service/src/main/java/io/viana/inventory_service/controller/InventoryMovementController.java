package io.viana.inventory_service.controller;

import io.viana.inventory_service.dto.MovementRequest; // DTO contendo a quantidade (amount) e a razão (reason) do movimento
import io.viana.inventory_service.service.InventoryService; // Serviço onde a lógica de negócio do estoque reside
import lombok.RequiredArgsConstructor; // Gera construtor para injeção de dependência

import org.springframework.http.HttpStatus; // Enum para códigos de status HTTP
import org.springframework.web.bind.annotation.*; // Anotações REST

/**
 * Define a classe como um Controller REST (combina @Controller e @ResponseBody).
 * Esta classe é dedicada a endpoints que modificam o estoque.
 */
@RestController
/**
 * Define o caminho base para todos os endpoints.
 * Todas as rotas começarão com /inventory.
 */
@RequestMapping("/inventory")
/**
 * Gera um construtor que injeta o InventoryService (campo final).
 */
@RequiredArgsConstructor
public class InventoryMovementController {

    // Injeção do serviço, que contém a lógica para manipulação do estoque.
    private final InventoryService service;

    /**
     * Mapeia requisições HTTP POST para: POST /inventory/{productId}/increase
     * Endpoint usado para adicionar quantidade ao estoque de um produto.
     *
     * @param productId O ID do produto extraído do caminho da URI (@PathVariable).
     * @param req O corpo da requisição é mapeado para o MovementRequest (contém amount e reason).
     *
     * @ResponseStatus(HttpStatus.OK): Retorna código 200 OK após a execução bem-sucedida.
     */
    @PostMapping("/{productId}/increase")
    @ResponseStatus(HttpStatus.OK)
    public void increaseStock(
            @PathVariable Long productId,
            @RequestBody MovementRequest req
    ) {
        // Chama o serviço para aumentar o estoque, passando a quantidade e a razão.
        service.increaseStock(productId, req.getAmount(), req.getReason());
    }

    /**
     * Mapeia requisições HTTP POST para: POST /inventory/{productId}/decrease
     * Endpoint usado para subtrair quantidade do estoque de um produto.
     *
     * @param productId O ID do produto extraído do caminho da URI (@PathVariable).
     * @param req O corpo da requisição é mapeado para o MovementRequest.
     *
     * @ResponseStatus(HttpStatus.OK): Retorna código 200 OK após a execução bem-sucedida.
     * Nota: O serviço deve validar se o estoque é suficiente antes de diminuir (lançando InsufficientStockException).
     */
    @PostMapping("/{productId}/decrease")
    @ResponseStatus(HttpStatus.OK)
    public void decreaseStock(
            @PathVariable Long productId,
            @RequestBody MovementRequest req
    ) {
        // Chama o serviço para diminuir o estoque, passando a quantidade e a razão.
        service.decreaseStock(productId, req.getAmount(), req.getReason());
    }
}