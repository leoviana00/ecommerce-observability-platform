package io.viana.inventory_service.model;

import jakarta.persistence.*; // Importa anotações JPA para mapeamento de banco de dados
import lombok.*; // Importa anotações do Lombok

/**
 * Entidade que representa o registro de estoque no 'inventory-service'.
 * Esta entidade armazena apenas o ID do produto e a quantidade de estoque atual.
 */
@Entity // Define a classe como uma entidade persistível no banco de dados.
@Table(name = "inventory", uniqueConstraints = {
    // Garante que não possa haver mais de um registro de estoque para o mesmo product_id.
    @UniqueConstraint(columnNames = "product_id")
})
@Data // Gera automaticamente Getters, Setters, toString(), equals() e hashCode().
@Builder // Permite a criação de objetos usando o padrão Builder.
@NoArgsConstructor // Gera o construtor padrão sem argumentos (necessário para JPA).
@AllArgsConstructor // Gera um construtor com todos os campos.
public class InventoryEntity {

    // Chave Primária (PK) interna do registro de inventário.
    @Id // Define este campo como a chave primária.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Especifica que o DB gerará o valor (auto-incremento).
    private Long id;

    // ID do produto ao qual este registro de estoque pertence.
    // É a chave estrangeira implícita que se relaciona com o 'product-service'.
    @Column(name = "product_id", nullable = false) // Garante a restrição NOT NULL em nível de DB.
    private Long productId;

    // A quantidade atual de itens em estoque.
    @Column(name = "stock_quantity", nullable = false) // Garante a restrição NOT NULL em nível de DB.
    private Integer stockQuantity;
}