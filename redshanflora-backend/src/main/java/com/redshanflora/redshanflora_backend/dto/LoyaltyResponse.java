package com.redshanflora.redshanflora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyResponse {
    private Integer points;
    private String membership;
    private String nextLevel;
    private Integer pointsToNext;
}
