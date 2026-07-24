package com.redshanflora.redshanflora_backend.dto.product;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BouquetDesignDto {
    private String style;
    private String bouquetStyle;
    private String wrappingId;
    private String ribbonId;
    private String sizeKey;
    private List<BouquetFlowerInstanceDto> flowers;
}

