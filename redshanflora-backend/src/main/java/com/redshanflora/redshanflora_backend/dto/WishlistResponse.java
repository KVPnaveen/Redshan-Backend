package com.redshanflora.redshanflora_backend.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {
    private Long wishlistId;
    private Long productId;
    private String productName;
    private BigDecimal price;
    private String imageUrl;
    private BigDecimal discountPercentage;
}
