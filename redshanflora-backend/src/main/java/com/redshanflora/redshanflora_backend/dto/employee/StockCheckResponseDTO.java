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
public class StockCheckResponseDTO {

    private Long itemId;

    private String itemName;

    private Integer requiredQuantity;

    private Integer availableStock;

    private Boolean stockAvailable;

    private String message;
}
