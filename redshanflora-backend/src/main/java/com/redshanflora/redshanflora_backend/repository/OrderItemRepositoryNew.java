package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepositoryNew extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

}