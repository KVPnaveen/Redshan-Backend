package com.redshanflora.redshanflora_backend.dto.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomizedOrderItemDTO {

    private Long itemId;


    private String itemName;

    private Integer quantity;

    private String status;

    private String flowerType;

    private Integer numberOfFlowers;

    private String bouquetStyle;


}