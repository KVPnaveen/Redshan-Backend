package com.redshanflora.redshanflora_backend.dto.product;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AddProductResponseDTO {
    private Long id;
    private Integer categoryId;
    private Integer subCategoryId;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
}





