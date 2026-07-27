package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Payment p JOIN p.order o " +
           "WHERE LOWER(p.paymentStatus) = :status AND p.paymentDate >= :startDate")
    BigDecimal sumTotalAmountByPaymentStatusAndPaymentDateAfter(
            @Param("status") String status, 
            @Param("startDate") Instant startDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Payment p JOIN p.order o " +
           "WHERE LOWER(p.paymentStatus) = :status AND p.paymentDate >= :startDate AND p.paymentDate < :endDate")
    BigDecimal sumTotalAmountByPaymentStatusAndPaymentDateBetween(
            @Param("status") String status, 
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate);
}

