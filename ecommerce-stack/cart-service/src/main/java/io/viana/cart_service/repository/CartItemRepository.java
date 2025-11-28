package io.viana.cart_service.repository;

import io.viana.cart_service.model.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findByCartId(Long cartId);

    Optional<CartItemEntity> findByCartIdAndProductId(Long cartId, Long productId);
}
