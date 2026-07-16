package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.LoyaltyCheckoutResponse;
import com.redshanflora.redshanflora_backend.dto.LoyaltyResponse;
import com.redshanflora.redshanflora_backend.dto.RedeemRequest;

public interface LoyaltyService {
    LoyaltyResponse getLoyaltyPointsByUserId(Long userId);
    LoyaltyCheckoutResponse getLoyaltyCheckoutDetails(Long userId);
    LoyaltyResponse redeemPoints(RedeemRequest request);
}
