package com.redshanflora.redshanflora_backend.dto.cart;

import com.redshanflora.redshanflora_backend.dto.product.BouquetDesignDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    private String title;
    private Integer quantity;
    private Boolean isCustom;
    private Double numericPrice;
    private BouquetDesignDto bouquetDesign;
}
