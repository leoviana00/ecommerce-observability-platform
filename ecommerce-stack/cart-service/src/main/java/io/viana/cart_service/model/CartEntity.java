package io.viana.cart_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CartEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
