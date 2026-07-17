package com.redshanflora.redshanflora_backend.entity;

import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "\"order\"")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;



    @Column(name = "order_date", nullable = false, updatable = false)
    private Instant orderDate;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false)
    @Builder.Default
    private MainOrderStatus orderStatus = MainOrderStatus.ORDER_CONFIRMED;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private OrderProcessing orderProcessing;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Payment payment;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY)
    private CustomizedBouquet customizedBouquet;

    @PrePersist
    protected void onCreate() {
        orderDate = Instant.now();
    }


}
