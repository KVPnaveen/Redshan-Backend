package com.redshanflora.redshanflora_backend.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerOrderItemDto {
    private String productName;
    private String imageUrl;
    private Integer quantity;
    private BigDecimal price;
}
