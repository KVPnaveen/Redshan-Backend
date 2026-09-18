package com.redshanflora.redshanflora_backend.dto.notification;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettingDto {
    private Boolean emailNotifications;
    private Boolean newOrderAlerts;
    private Boolean lowStockAlerts;
    private Boolean monthlyReportEmails;
    private Integer lowStockThreshold;
}
