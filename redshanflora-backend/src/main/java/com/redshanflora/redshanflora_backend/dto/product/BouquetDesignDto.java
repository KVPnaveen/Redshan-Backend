package com.redshanflora.redshanflora_backend.dto.product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BouquetDesignDto {
    private String style;
    private String wrappingId;
    private String ribbonId;
    private List<FlowerDesignDto> flowers;
}
