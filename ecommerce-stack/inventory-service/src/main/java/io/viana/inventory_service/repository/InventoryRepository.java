package io.viana.inventory_service.repository;

import java.util.Optional; // Classe para lidar com valores que podem estar ausentes
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA
import io.viana.inventory_service.model.InventoryEntity; // A Entidade JPA gerenciada por este repositório

/**
 * Interface de Repositório para a entidade InventoryEntity.
 *
 * Estende JpaRepository, fornecendo automaticamente métodos CRUD (Create, Read, Update, Delete)
 * básicos e poderosos recursos de consulta baseados no nome do método.
 *
 * @param <InventoryEntity> O tipo da Entidade gerenciada.
 * @param <Long> O tipo da Chave Primária da Entidade (InventoryEntity.id).
 */
public interface InventoryRepository extends JpaRepository<InventoryEntity, Long> {

    /**
     * Consulta customizada gerada pelo Spring Data JPA a partir do nome do método.
     *
     * Objetivo: Buscar o registro de estoque que corresponde ao ID de produto fornecido.
     * Como a entidade InventoryEntity tem uma restrição de unicidade em 'product_id',
     * 'findTopBy' é usado para buscar o primeiro (e único) resultado.
     *
     * @param productId O ID do produto a ser buscado.
     * @return Um Optional contendo o InventoryEntity se encontrado, ou Optional.empty() caso contrário.
     */
    Optional<InventoryEntity> findTopByProductId(Long productId);

    /**
     * Consulta customizada gerada pelo Spring Data JPA.
     *
     * Objetivo: Verificar rapidamente se já existe um registro de estoque para o ID de produto.
     * O Spring Data otimiza isso para retornar um booleano de forma eficiente (sem retornar o objeto inteiro).
     *
     * @param productId O ID do produto a ser verificado.
     * @return true se o registro existir, false caso contrário.
     */
    boolean existsByProductId(Long productId);
}