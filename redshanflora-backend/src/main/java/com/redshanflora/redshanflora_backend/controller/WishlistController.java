package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.wishlist.WishlistCountResponse;
import com.redshanflora.redshanflora_backend.dto.wishlist.WishlistRequest;
import com.redshanflora.redshanflora_backend.dto.wishlist.WishlistResponse;
import com.redshanflora.redshanflora_backend.service.WishlistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@Slf4j
public class WishlistController {

    private final WishlistService wishlistService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addToWishlist(@Valid @RequestBody WishlistRequest request) {
        log.info("Received POST request to add product to wishlist: userId={}, productId={}", request.getUserId(), request.getProductId());
        WishlistResponse response = wishlistService.addToWishlist(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Product added to wishlist successfully",
                "data", response
        ));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WishlistResponse>> getWishlistByUserId(@PathVariable Long userId) {
        log.info("Received GET request to retrieve wishlist for userId={}", userId);
        List<WishlistResponse> wishlist = wishlistService.getWishlistByUserId(userId);
        return ResponseEntity.ok(wishlist);
    }

    @DeleteMapping("/{wishlistId}")
    public ResponseEntity<Map<String, String>> removeFromWishlist(@PathVariable Long wishlistId) {
        log.info("Received DELETE request to remove wishlistId={}", wishlistId);
        wishlistService.removeFromWishlist(wishlistId);
        return ResponseEntity.ok(Map.of("message", "Product removed from wishlist successfully"));
    }

    @GetMapping("/count/{userId}")
    public ResponseEntity<WishlistCountResponse> getWishlistCount(@PathVariable Long userId) {
        log.info("Received GET request to count wishlist items for userId={}", userId);
        long count = wishlistService.getWishlistCount(userId);
        return ResponseEntity.ok(WishlistCountResponse.builder().count(count).build());
    }
}
