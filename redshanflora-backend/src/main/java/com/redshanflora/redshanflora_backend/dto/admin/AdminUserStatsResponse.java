package com.redshanflora.redshanflora_backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminUserStatsResponse {
    private long totalCustomers;
    private double customerIncreasePercentage;
    private long totalManagers;
    private long totalEmployees;
}
