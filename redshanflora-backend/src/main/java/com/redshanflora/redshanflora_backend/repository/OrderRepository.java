package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByEmployeeIsNotNull();
    List<Order> findByEmployeeIsNull();

    List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);
    Optional<Order> findByIdAndCustomer(Long id, Customer customer);

    long countByOrderStatus(MainOrderStatus orderStatus);
    long countByOrderDateBetween(Instant start, Instant end);
}


