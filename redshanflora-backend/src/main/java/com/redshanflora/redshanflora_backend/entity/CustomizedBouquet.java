package com.redshanflora.redshanflora_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "customized_bouquet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomizedBouquet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "custom_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "premium_blooms", length = 150)
    private String premiumBlooms;

    @Column(name = "boquet_style", length = 100)
    private String bouquetStyle;

    @Column(name = "decorative_foliage", length = 50)
    private String decorativeFoliage;

    @Column(name = "wrapping", length = 100)
    private String wrapping;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", unique = true)
    private Order order;

    @Column(name = "custom_bouquet_snapshot", columnDefinition = "TEXT")
    private String customBouquetSnapshot;

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @PrePersist
    protected void onCreate() {
        createdDate = Instant.now();
    }
}

