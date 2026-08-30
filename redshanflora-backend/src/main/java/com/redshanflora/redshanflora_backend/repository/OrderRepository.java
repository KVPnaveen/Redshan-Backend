package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import com.redshanflora.redshanflora_backend.enums.SubStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("SELECT o FROM Order o WHERE o.employee IS NOT NULL AND o.workingStatus != 'Finished'")
    List<Order> findFinishedOrdersWithEmployee();

    List<Order> findByEmployeeIsNull();

    List<Order> findByCustomerOrderByOrderDateDesc(Customer customer);

    Optional<Order> findByIdAndCustomer(Long id, Customer customer);

    long countByOrderStatusAndOrderDateBetween(
            MainOrderStatus orderStatus,
            Instant start,
            Instant end
    );

    List<Order> findByEmployeeId(Long employeeId);

    List<Order> findByEmployeeIdAndOrderStatus(
            Long employeeId,
            MainOrderStatus orderStatus
    );

    List<Order> findByEmployeeIdAndOrderStatusNot(
            Long employeeId,
            MainOrderStatus orderStatus
    );

    // =========================================================
    // NORMAL ASSIGNED ORDERS
    // customizedBouquet IS NULL
    // =========================================================

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.employee.id = :employeeId
          AND o.customizedBouquet IS NULL
          AND o.orderStatus <> :status
    """)
    List<Order> findNormalAssignedOrders(
            @Param("employeeId") Long employeeId,
            @Param("status") MainOrderStatus status
    );

    @Query("""
    SELECT DISTINCT o
    FROM Order o
    JOIN OrderItem oi ON oi.order = o
    WHERE o.employee.id = :employeeId
      AND oi.itemStatus = :itemStatus
      AND o.orderStatus <> :orderStatus
""")
    List<Order> findAssignedOrdersWithPendingItems(
            @Param("employeeId") Long employeeId,
            @Param("itemStatus") SubStatus itemStatus,
            @Param("orderStatus") MainOrderStatus orderStatus
    );

    // =========================================================
    // CUSTOMIZED ASSIGNED ORDERS
    // customizedBouquet IS NOT NULL
    // =========================================================

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.employee.id = :employeeId
          AND o.customizedBouquet IS NOT NULL
          AND o.orderStatus <> :status
    """)
    List<Order> findCustomizedAssignedOrders(
            @Param("employeeId") Long employeeId,
            @Param("status") MainOrderStatus status
    );

    // =========================================================
    // NORMAL COMPLETED ORDERS
    // customizedBouquet IS NULL
    // =========================================================

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.employee.id = :employeeId
          AND o.customizedBouquet IS NULL
          AND o.orderStatus = :status
    """)
    List<Order> findNormalCompletedOrders(
            @Param("employeeId") Long employeeId,
            @Param("status") MainOrderStatus status
    );

    // =========================================================
    // CUSTOMIZED COMPLETED ORDERS
    // customizedBouquet IS NOT NULL
    // =========================================================

    @Query("""
        SELECT o
        FROM Order o
        WHERE o.employee.id = :employeeId
          AND o.customizedBouquet IS NOT NULL
          AND o.orderStatus = :status
    """)
    List<Order> findCustomizedCompletedOrders(
            @Param("employeeId") Long employeeId,
            @Param("status") MainOrderStatus status
    );

    long countByOrderStatus(MainOrderStatus orderStatus);

    long countByOrderDateBetween(Instant start, Instant end);

    List<Order> findByOrderStatus(MainOrderStatus orderStatus);


    List<Order> findTop5ByOrderByOrderDateDesc();

    long countByOrderDateGreaterThanEqualAndOrderDateLessThan(
            Instant startDate,
            Instant endDate
    );
}