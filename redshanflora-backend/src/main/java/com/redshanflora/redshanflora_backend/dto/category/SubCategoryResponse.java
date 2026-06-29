package com.redshanflora.redshanflora_backend.dto.category;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubCategoryResponse {
    private Long id;
    private Long categoryId;
    private String subCategoryName;
    private String description;
    private LocalDateTime createdAt;
}
