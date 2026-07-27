package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByOrderId(Long id);

    @Query("SELECT c.categoryName, SUM(oi.quantity * oi.price) FROM OrderItem oi " +
           "JOIN oi.product p " +
           "JOIN p.category c " +
           "JOIN oi.order o " +
           "JOIN o.payment pay " +
           "WHERE LOWER(pay.paymentStatus) = :status " +
           "AND pay.paymentDate >= :startDate " +
           "GROUP BY c.categoryName")
    List<Object[]> findCategoryRevenueByPaymentStatusAndDateAfter(
            @Param("status") String status,
            @Param("startDate") Instant startDate);

    @Query("SELECT c.categoryName, SUM(oi.quantity * oi.price) FROM OrderItem oi " +
           "JOIN oi.product p " +
           "JOIN p.category c " +
           "JOIN oi.order o " +
           "JOIN o.payment pay " +
           "WHERE LOWER(pay.paymentStatus) = :status " +
           "AND pay.paymentDate >= :startDate AND pay.paymentDate < :endDate " +
           "GROUP BY c.categoryName")
    List<Object[]> findCategoryRevenueByPaymentStatusAndDateBetween(
            @Param("status") String status,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);
}


