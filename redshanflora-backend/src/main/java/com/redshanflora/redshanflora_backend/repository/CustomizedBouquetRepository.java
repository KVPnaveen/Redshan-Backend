package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.CustomizedBouquet;
import com.redshanflora.redshanflora_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CustomizedBouquetRepository extends JpaRepository<CustomizedBouquet, Long> {
    Optional<CustomizedBouquet> findByOrder(Order order);
}

