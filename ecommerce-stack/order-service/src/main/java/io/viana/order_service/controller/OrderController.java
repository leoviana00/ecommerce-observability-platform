package io.viana.order_service.controller;

import io.viana.order_service.dto.CreateOrderRequest; // DTO usado para receber dados para criar um novo pedido
import io.viana.order_service.dto.OrderResponse; // DTO usado para retornar os detalhes do pedido
import io.viana.order_service.service.OrderService; // Serviço de negócio
import lombok.RequiredArgsConstructor; // Gera construtor para injeção de dependência
import org.springframework.web.bind.annotation.*; // Anotações REST

/**
 * Define a classe como um Controller REST (combina @Controller e @ResponseBody).
 * Responsável por receber requisições HTTP e delegar a lógica de negócio ao OrderService.
 */
@RestController
/**
 * Define o caminho base para todos os endpoints deste controller.
 * Todas as rotas começarão com /orders.
 */
@RequestMapping("/orders")
/**
 * Usa o Lombok para gerar um construtor que injeta o OrderService (campo final).
 */
@RequiredArgsConstructor
public class OrderController {

    // Injeção do serviço, onde a lógica de criação e consulta de pedidos reside.
    private final OrderService orderService;

    /**
     * Mapeia requisições HTTP POST para: POST /orders
     * Endpoint usado para iniciar o fluxo de criação de um novo pedido.
     *
     * @param request O corpo da requisição é mapeado para o DTO CreateOrderRequest (@RequestBody).
     * @return Retorna um OrderResponse com os detalhes iniciais do pedido (ex: status: PENDING).
     */
    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        // Delega ao serviço para iniciar a transação SAGA (reserva de estoque, pagamento, etc.)
        return orderService.createOrder(request);
    }

    /**
     * Mapeia requisições HTTP GET para: GET /orders/{orderId}
     * Endpoint usado para consultar os detalhes e o status atual de um pedido.
     *
     * @param orderId O ID do pedido extraído do caminho da URI (@PathVariable).
     * @return Retorna um OrderResponse com os detalhes completos do pedido.
     */
    @GetMapping("/{orderId}")
    public OrderResponse getOrder(@PathVariable Long orderId) {
        // Delega ao serviço para buscar o pedido no banco de dados.
        return orderService.getOrder(orderId);
    }
}