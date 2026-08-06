package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.time.Instant;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByUser(User user);
    Optional<Customer> findByUser_Id(Long userId);
    Optional<Customer> findByUserId(Long userId);

    @Query("SELECT COUNT(c) FROM Customer c WHERE (SELECT COUNT(o) FROM Order o WHERE o.customer = c AND o.orderDate <= :periodEnd) > 1")
    long countReturningCustomers(@Param("periodEnd") Instant periodEnd);
}
