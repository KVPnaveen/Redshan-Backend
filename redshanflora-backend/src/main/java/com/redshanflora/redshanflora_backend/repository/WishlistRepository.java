package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findByUser_IdAndProduct_Id(Long userId, Long productId);
    List<Wishlist> findByUser_Id(Long userId);
    long countByUser_Id(Long userId);
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
}
