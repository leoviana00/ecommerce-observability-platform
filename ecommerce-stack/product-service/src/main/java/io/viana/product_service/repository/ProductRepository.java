package io.viana.product_service.repository;

import io.viana.product_service.model.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository; // Interface base do Spring Data JPA
import org.springframework.stereotype.Repository; // Marca a interface como componente de persistência do Spring

import java.util.List;

/**
 * Repositório JPA para a entidade ProductEntity.
 * * * JpaRepository<ProductEntity, Long>: Herança que fornece os métodos
 * CRUD (salvar, buscar por ID, listar todos, etc.) prontos para uso.
 * - ProductEntity: A entidade que está sendo gerenciada.
 * - Long: O tipo da chave primária (ID) da entidade.
 */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    // --- Métodos de Consulta Customizados (Query Methods) ---

    /**
     * Busca produtos cujo nome contém a string fornecida, ignorando o caso (maiúsculas/minúsculas).
     * * * O Spring Data JPA traduz automaticamente o nome do método:
     * - findBy: Indica uma operação de busca.
     * - NameContaining: Filtra pela coluna 'name' usando LIKE %name%.
     * - IgnoreCase: Adiciona insensibilidade ao caso na comparação.
     * * * @param name A substring a ser buscada no nome do produto.
     * @return Lista de produtos que correspondem ao critério.
     */
    List<ProductEntity> findByNameContainingIgnoreCase(String name);

    /**
     * Busca produtos onde o valor do campo 'stock' é maior do que o valor fornecido.
     * * * NOTA DE ARQUITETURA CRÍTICA: Este método é INCOMPATÍVEL com a arquitetura 
     * revisada, onde o campo 'stock' foi removido do ProductEntity. 
     * Ele DEVE ser REMOVIDO deste repositório, pois a responsabilidade de estoque
     * pertence inteiramente ao 'inventory-service'.
     * * * @param stock O valor mínimo de estoque a ser considerado disponível.
     * @return Lista de produtos que atendem à condição de estoque.
     */
    // List<ProductEntity> findByStockGreaterThan(int stock); // REMOVER/COMENTAR ESTE MÉTODO
}
