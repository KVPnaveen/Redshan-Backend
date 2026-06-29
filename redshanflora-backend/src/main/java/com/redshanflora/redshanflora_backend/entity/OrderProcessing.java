package com.redshanflora.redshanflora_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "order_processing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderProcessing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "processing_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(name = "main_status", nullable = false)
    @Builder.Default
    private MainOrderStatus mainStatus = MainOrderStatus.PROCESSING;

    @Enumerated(EnumType.STRING)
    @Column(name = "sub_status", nullable = false)
    @Builder.Default
    private SubStatus subStatus = SubStatus.START;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_employee")
    private Employee updatedByEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by_manager")
    private Manager updatedByManager;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
