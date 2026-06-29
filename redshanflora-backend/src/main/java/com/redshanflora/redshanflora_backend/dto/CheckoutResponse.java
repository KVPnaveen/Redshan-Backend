package com.redshanflora.redshanflora_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckoutResponse {
    private String merchantId;
    private String orderId;
    private Double amount;
    private String currency;
    private String hash;
    private String itemsName;
}
