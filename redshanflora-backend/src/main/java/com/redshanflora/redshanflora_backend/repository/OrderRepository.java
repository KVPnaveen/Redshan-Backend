package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByEmployeeIsNotNull();

    List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);
    Optional<Order> findByIdAndCustomer(Long id, Customer customer);

}

