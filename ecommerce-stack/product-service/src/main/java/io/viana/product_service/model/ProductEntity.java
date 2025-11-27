package io.viana.product_service.model;

import jakarta.persistence.*;           // Importa anotações JPA para mapeamento de banco de dados
import jakarta.validation.constraints.NotNull; // Importa anotação para validação
import lombok.*;                        // Importa anotações do Lombok

/**
 * 🏷️ ProductEntity (Catálogo)
 *
 * Esta entidade representa o **Catálogo de Produtos** no 'product-service'.
 * Ela armazena apenas dados descritivos/de catálogo (Nome, Preço, Descrição).
 *
 * 🎯 **Princípio SRP:** O controle de estoque (**stock**) foi intencionalmente
 * **REMOVIDO** e delegado ao 'inventory-service' (ou similar), garantindo
 * que o 'product-service' se preocupe apenas com a gestão do catálogo.
 */

// === Anotações JPA ===
@Entity // Define a classe como uma entidade persistível no banco de dados.
@Table(name = "products") // Mapeia esta entidade para a tabela chamada "products".

// === Anotações Lombok ===
@Data                // Gera automaticamente Getters, Setters, toString(), equals() e hashCode().
@NoArgsConstructor   // Gera o construtor padrão sem argumentos (necessário para JPA).
@AllArgsConstructor  // Gera um construtor com todos os campos (útil para Builder/Testes).
@Builder             // Permite a criação de objetos usando o padrão Builder.
public class ProductEntity {

    // 🔑 Chave Primária (PK)
    @Id // Define este campo como a chave primária.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Especifica que o DB gerará o valor (auto-incremento).
    private Long id;

    // 🏷️ Nome do Produto
    @NotNull // Garante que o campo não seja nulo em nível de aplicação (validação).
    @Column(nullable = false) // Garante a restrição NOT NULL em nível de banco de dados.
    private String name;

    // 📝 Descrição
    @Column(length = 1000) // Define o tamanho máximo da coluna para 1000 caracteres.
    private String description;

    // 💰 Preço
    @NotNull
    @Column(nullable = false)
    private Double price;

    // --- CAMPO REMOVIDO PARA DELEGAR AO INVENTORY SERVICE ---
    // private Integer stock;
}