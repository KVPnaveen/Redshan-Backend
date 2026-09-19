package com.redshanflora.redshanflora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogDto {
    private Long id;
    private String type;
    private String activityType;
    private String title;
    private String description;
    private String performedBy;
    private String userRole;
    private Instant timestamp;
    private String time;
}
