package com.redshanflora.redshanflora_backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryResponse {
    private Long id;
    private String categoryName;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
}
