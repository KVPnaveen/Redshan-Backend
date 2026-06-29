package com.redshanflora.redshanflora_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long subCategoryId;
    private String subCategoryName;
    private String productName;
    private String description;
    private BigDecimal price;
    private Integer stockQuantity;
    private String imageUrl;
    private String view360Url;
    private BigDecimal discountPercentage;
    private Boolean featured;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
