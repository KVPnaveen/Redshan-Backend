package com.redshanflora.redshanflora_backend.dto.product;

import com.redshanflora.redshanflora_backend.dto.category.CategoryResponse;
import com.redshanflora.redshanflora_backend.dto.category.SubCategoryResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    @JsonProperty("productId")
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
    private BigDecimal discountPercentage;
    private CategoryResponse category;
    private SubCategoryResponse subCategory;
    private String modelUrl;
}
