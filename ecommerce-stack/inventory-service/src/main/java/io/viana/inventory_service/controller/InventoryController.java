package io.viana.inventory_service.controller;

import io.viana.inventory_service.dto.InventoryResponse; // DTO que será retornado (ex: {productId, isInStock})
import io.viana.inventory_service.service.InventoryService; // Serviço onde a lógica de negócio reside
import lombok.RequiredArgsConstructor; // Gera construtor para injeção de dependência
import org.springframework.http.HttpStatus; // Enum para códigos de status HTTP
import org.springframework.web.bind.annotation.*; // Anotações REST

/**
 * Define esta classe como um Controller REST (combina @Controller e @ResponseBody).
 */
@RestController
/**
 * Define o caminho base para todos os endpoints deste controller.
 * Ex: Todas as rotas começarão com /inventory.
 */
@RequestMapping("/inventory")
/**
 * Usa o Lombok para gerar um construtor que injeta o InventoryService (campo final).
 */
@RequiredArgsConstructor
public class InventoryController {

    // Injeção do serviço, onde a lógica de consulta de estoque reside.
    private final InventoryService inventoryService;

    /**
     * Mapeia requisições HTTP GET para a URI: GET /inventory/{productId}
     * É usado para verificar o status do inventário de um produto específico.
     *
     * @param productId O ID do produto extraído do caminho da URI (@PathVariable).
     * @return Retorna um InventoryResponse (contendo o ID e o status de estoque).
     *
     * @ResponseStatus(HttpStatus.OK): Garante que o código de status HTTP 200 seja retornado
     * em caso de sucesso (mesmo que o produto não esteja em estoque, a consulta foi bem-sucedida).
     */
    @GetMapping("/{productId}")
    @ResponseStatus(HttpStatus.OK)
    public InventoryResponse getInventoryByProductId(@PathVariable Long productId) {
        // Chama o serviço para buscar os dados de estoque e mapear para o DTO de resposta.
        return inventoryService.getInventoryResponse(productId);
    }
}