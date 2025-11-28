package io.viana.order_service.model;

import jakarta.persistence.*; // Importa anotações JPA para mapeamento de banco de dados
import lombok.*; // Importa anotações do Lombok

/**
 * Entidade que representa um item individual dentro de um pedido.
 *
 * Esta entidade armazena os dados do produto (ID) e as informações
 * financeiras (preço e quantidade) no momento da compra (snapshot).
 */
@Entity // Define a classe como uma entidade persistível.
@Table(name = "order_items") // Nome da tabela no banco de dados.
@Getter @Setter // Gera Getters e Setters.
@NoArgsConstructor @AllArgsConstructor @Builder // Gera construtores e o padrão Builder.
public class OrderItemEntity {

    // Chave Primária (PK) do Item do Pedido.
    @Id // Define este campo como a chave primária.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Especifica auto-incremento.
    private Long id;

    // Chave Estrangeira: ID do Pedido ao qual este item pertence.
    // É o campo de correlação com a OrderEntity.
    private Long orderId;
    
    // ID do produto (referência ao product-service).
    // Usado para saber qual item está sendo comprado.
    private Long productId;

    // Quantidade comprada deste item.
    // Essencial para o cálculo do total do item e do pedido.
    private Integer quantity;

    // Preço unitário do produto no momento em que o pedido foi feito.
    // É o snapshot do preço, garantindo que o valor não mude se o preço de catálogo for atualizado.
    private Double unitPrice; // nome correto
    
    // NOTA: Em sistemas mais robustos, orderId seria mapeado via @ManyToOne com OrderEntity.
}