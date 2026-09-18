package com.redshanflora.redshanflora_backend.dto.notification;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDto {
    private Long id;
    private String type;
    private String title;
    private String message;
    private Long referenceId;
    private Boolean isRead;
    private Instant createdAt;
}
