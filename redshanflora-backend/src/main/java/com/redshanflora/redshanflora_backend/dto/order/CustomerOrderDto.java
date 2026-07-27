package com.redshanflora.redshanflora_backend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderDto {
    private Long orderId;
    private Instant orderDate;
    private BigDecimal totalAmount;
    private String mainStatus;
    private String currentSubStatus;
    private List<CustomerOrderItemDto> items;
}
