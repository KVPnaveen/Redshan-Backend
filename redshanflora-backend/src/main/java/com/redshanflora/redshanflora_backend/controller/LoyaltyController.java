package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.LoyaltyCheckoutResponse;
import com.redshanflora.redshanflora_backend.dto.LoyaltyResponse;
import com.redshanflora.redshanflora_backend.dto.RedeemRequest;
import com.redshanflora.redshanflora_backend.service.LoyaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loyalty")
@RequiredArgsConstructor
public class LoyaltyController {

    private final LoyaltyService loyaltyService;

    @GetMapping("/{userId}")
    public ResponseEntity<LoyaltyResponse> getLoyaltyPointsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(loyaltyService.getLoyaltyPointsByUserId(userId));
    }

    @GetMapping("/redeem/{userId}")
    public ResponseEntity<LoyaltyCheckoutResponse> getLoyaltyCheckoutDetails(@PathVariable Long userId) {
        return ResponseEntity.ok(loyaltyService.getLoyaltyCheckoutDetails(userId));
    }

    @PostMapping("/redeem")
    public ResponseEntity<LoyaltyResponse> redeemPoints(@RequestBody RedeemRequest request) {
        return ResponseEntity.ok(loyaltyService.redeemPoints(request));
    }
}
