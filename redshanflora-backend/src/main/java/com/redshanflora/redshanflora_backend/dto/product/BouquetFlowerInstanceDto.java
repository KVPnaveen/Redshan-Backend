package com.redshanflora.redshanflora_backend.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BouquetFlowerInstanceDto {
    private String instanceId;
    private Long productId;
    private CoordinateDto position;
    private CoordinateDto rotation;
    private CoordinateDto scale;
    private Boolean isManuallyEdited;
}
