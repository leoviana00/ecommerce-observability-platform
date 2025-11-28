package io.viana.order_service.repository;

import io.viana.order_service.model.OrderEntity; // A Entidade JPA gerenciada
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA

/**
 * Interface de Repositório para a entidade OrderEntity.
 *
 * Estende JpaRepository, fornecendo automaticamente métodos CRUD (Create, Read, Update, Delete)
 * e recursos de consulta poderosa baseados no nome do método.
 *
 * @param <OrderEntity> O tipo da Entidade gerenciada.
 * @param <Long> O tipo da Chave Primária da Entidade (OrderEntity.id).
 */
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    
    // Nenhuma consulta customizada é necessária aqui, pois as operações básicas
    // (findById, save, findAll) são fornecidas automaticamente pela JpaRepository.
}