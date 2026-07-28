package com.redshanflora.redshanflora_backend.repository;

import com.redshanflora.redshanflora_backend.entity.Order;
import com.redshanflora.redshanflora_backend.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrder(Order order);

    List<OrderItem> findByOrderId(Long id);

    @Query("""
        SELECT COUNT(oi.id)
        FROM OrderItem oi
        WHERE oi.order.id = :orderId
    """)
    Long countItemsByOrderId(
            @Param("orderId") Long orderId
    );


    @Query("""
        SELECT COALESCE(SUM(oi.quantity), 0)
        FROM OrderItem oi
        WHERE oi.order.id = :orderId
    """)
    Long sumQuantityByOrderId(
            @Param("orderId") Long orderId
    );
}

