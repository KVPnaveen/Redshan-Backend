package com.redshanflora.redshanflora_backend.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BouquetFlowerDto {
    private Long productId;
    private Integer quantity;
}
