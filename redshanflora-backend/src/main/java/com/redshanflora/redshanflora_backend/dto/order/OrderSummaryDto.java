package com.redshanflora.redshanflora_backend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {
    private Long orderId;
    private Instant orderDate;
    private BigDecimal totalAmount;
    private String orderStatus;
    private String paymentStatus;
}
