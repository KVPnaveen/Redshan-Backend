package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByProduct_IdOrderByReviewDateDesc(Long productId);

    Optional<Review> findByCustomer_IdAndProduct_Id(Long customerId, Long productId);

    boolean existsByCustomer_IdAndProduct_Id(Long customerId, Long productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    long countByProduct_Id(Long productId);
}
