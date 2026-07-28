package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.OrderProcessing;
import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderProcessingRepository extends JpaRepository<OrderProcessing, Long> {

    Optional<OrderProcessing> findByOrderId(Long orderId);

    List<OrderProcessing> findByMainStatus(MainOrderStatus mainStatus);

}