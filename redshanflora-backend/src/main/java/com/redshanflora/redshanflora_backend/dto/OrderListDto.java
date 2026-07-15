package com.redshanflora.redshanflora_backend.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class OrderListDto {

    private Long orderId;
    private String customerName;
    private String itemName;
    private BigDecimal price;
    private String type;
}
