package io.viana.order_service.repository;

import io.viana.order_service.model.OrderItemEntity; // A Entidade JPA gerenciada
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA

import java.util.List; // Importa a classe List

/**
 * Interface de Repositório para a entidade OrderItemEntity.
 *
 * Estende JpaRepository, fornecendo automaticamente métodos CRUD (Create, Read, Update, Delete).
 *
 * @param <OrderItemEntity> O tipo da Entidade gerenciada.
 * @param <Long> O tipo da Chave Primária da Entidade (OrderItemEntity.id).
 */
public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    /**
     * Consulta customizada gerada pelo Spring Data JPA a partir do nome do método.
     *
     * Objetivo: Buscar todos os itens de pedido que pertencem a um OrderId específico.
     * Esta consulta é crucial para carregar os detalhes de um pedido na resposta da API.
     *
     * @param orderId O ID do pedido pai.
     * @return Uma lista de OrderItemEntity associados ao orderId.
     */
    List<OrderItemEntity> findByOrderId(Long orderId);
}