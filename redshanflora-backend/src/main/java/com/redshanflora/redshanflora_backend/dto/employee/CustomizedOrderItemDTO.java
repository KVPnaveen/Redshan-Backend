package com.redshanflora.redshanflora_backend.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomizedOrderItemDTO {

    private Long itemId;
    private String itemName;
    private Integer quantity;
    private String status;
    private String flowerType;
    private Integer numberOfFlowers;
    private String bouquetStyle;
    private String sizeLabel;
    private String wrapping;
    private Map<String, Integer> flowerQuantities;
    private String imageUrl;
}