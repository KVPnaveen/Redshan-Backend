package com.redshanflora.redshanflora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyCheckoutResponse {
    private Integer points;
    private String membership;
    private Integer membershipDiscount;
    private Integer redeemablePoints;
    private Integer redeemDiscount;
    private Integer remainingPoints;
}
