package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCart_IdAndProduct_IdAndSelectedColor(Long cartId, Long productId, String selectedColor);
}
