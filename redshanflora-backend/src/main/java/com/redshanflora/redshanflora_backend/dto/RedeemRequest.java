package com.redshanflora.redshanflora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedeemRequest {
    private Long userId;
    private Integer points;
}
