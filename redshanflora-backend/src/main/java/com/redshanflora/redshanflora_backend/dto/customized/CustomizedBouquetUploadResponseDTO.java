package com.redshanflora.redshanflora_backend.dto.customized;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomizedBouquetUploadResponseDTO {
    private Long customerId;
    private Long orderId;
    private String fileName;
    private String relativePath;
}
