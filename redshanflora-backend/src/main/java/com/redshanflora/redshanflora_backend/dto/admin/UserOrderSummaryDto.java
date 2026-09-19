package com.redshanflora.redshanflora_backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrderSummaryDto {
    private Long id;
    private String orderCode;
    private Instant orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String workingStatus;
}
