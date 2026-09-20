package com.redshanflora.redshanflora_backend.dto.review;

import lombok.*;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long reviewId;
    private Long customerId;
    private Long userId;
    private String customerName;
    private Long productId;
    private Integer rating;
    private String comment;
    private Instant reviewDate;
}
