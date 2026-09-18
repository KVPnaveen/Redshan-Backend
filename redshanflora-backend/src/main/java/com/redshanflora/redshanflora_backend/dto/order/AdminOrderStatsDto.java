package com.redshanflora.redshanflora_backend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminOrderStatsDto {
    private long pending;
    private long processing;
    private long completed;
}
