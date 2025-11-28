package io.viana.order_service.model;

import jakarta.persistence.*; // Importa anotações JPA para mapeamento de banco de dados
import lombok.*; // Importa anotações do Lombok

import java.math.BigDecimal; // Importação não utilizada, mas geralmente boa para valores monetários
import java.time.LocalDateTime; // Para data e hora de criação
import java.time.Instant; // Para timestamp universal (momento em que o pagamento foi efetuado)

/**
 * Entidade principal que representa um pedido no banco de dados do 'order-service'.
 *
 * O 'order-service' é a fonte de verdade para o ciclo de vida e status do pedido.
 */
@Entity // Define a classe como uma entidade persistível.
@Table(name = "orders") // Nome da tabela no banco de dados.
@Getter @Setter // Gera Getters e Setters para todos os campos.
@NoArgsConstructor @AllArgsConstructor @Builder // Gera construtores e o padrão Builder.
public class OrderEntity {
    
    // Chave Primária (PK) do Pedido.
    @Id // Define este campo como a chave primária.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Especifica auto-incremento.
    private Long id;

    // ID do usuário que fez o pedido. Chave de correlação para o 'user-service'.
    private Long userId;

    // Valor total do pedido.
    // Nota: Recomenda-se o uso de BigDecimal para valores monetários em produção para evitar erros de ponto flutuante.
    private Double total;

    // Status atual do pedido (PENDING, PAID, SHIPPED, PAYMENT_FAILED, etc.).
    // Este campo reflete o estado atual do SAGA.
    private String status;

    // Data e hora local em que o pedido foi criado.
    private LocalDateTime createdAt;

    // Novo campo: Timestamp universal em que o pagamento foi confirmado com sucesso.
    // Útil para auditoria e sincronização entre fusos horários.
    private Instant paidAt;
    
    // --- Relacionamento com OrderItemEntity (Geralmente um @OneToMany) ---
    // (A ser adicionado se houver uma entidade OrderItem separada)
}