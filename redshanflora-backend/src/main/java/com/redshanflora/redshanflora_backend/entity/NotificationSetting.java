package com.redshanflora.redshanflora_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "notification_setting")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "email_notifications", nullable = false)
    @Builder.Default
    private Boolean emailNotifications = true;

    @Column(name = "new_order_alerts", nullable = false)
    @Builder.Default
    private Boolean newOrderAlerts = true;

    @Column(name = "low_stock_alerts", nullable = false)
    @Builder.Default
    private Boolean lowStockAlerts = true;

    @Column(name = "monthly_report_emails", nullable = false)
    @Builder.Default
    private Boolean monthlyReportEmails = true;

    @Column(name = "low_stock_threshold", nullable = false)
    @Builder.Default
    private Integer lowStockThreshold = 5;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    @PreUpdate
    protected void onSaveOrUpdate() {
        this.updatedAt = Instant.now();
        if (emailNotifications == null) emailNotifications = true;
        if (newOrderAlerts == null) newOrderAlerts = true;
        if (lowStockAlerts == null) lowStockAlerts = true;
        if (monthlyReportEmails == null) monthlyReportEmails = true;
        if (lowStockThreshold == null) lowStockThreshold = 5;
    }
}
