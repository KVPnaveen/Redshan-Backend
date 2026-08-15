package com.redshanflora.redshanflora_backend.dto;

import com.redshanflora.redshanflora_backend.enums.MainOrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusDTO {

    private Long orderId;
    private String customerName;
    private BigDecimal totalAmount;
    private MainOrderStatus orderStatus;
}