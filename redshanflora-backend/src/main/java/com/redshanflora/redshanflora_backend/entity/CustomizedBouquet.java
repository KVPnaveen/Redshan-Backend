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

    @Column(name = "created_date", nullable = false, updatable = false)
    private Instant createdDate;

    @PrePersist
    protected void onCreate() {
        createdDate = Instant.now();
    }
}
