package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.cart.AddToCartRequest;
import com.redshanflora.redshanflora_backend.dto.cart.CartResponse;
import com.redshanflora.redshanflora_backend.dto.cart.UpdateCartItemQuantityRequest;
import com.redshanflora.redshanflora_backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class CartController {

    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponse> addToCart(@Valid @RequestBody AddToCartRequest request) {
        log.info("Received POST request to add product to cart: productId={}, quantity={}, color={}",
                request.getProductId(), request.getQuantity(), request.getSelectedColor());
        CartResponse response = cartService.addToCart(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCurrentUserCart() {
        log.info("Received GET request to retrieve current user's cart");
        CartResponse response = cartService.getCurrentUserCart();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> updateQuantity(
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemQuantityRequest request) {
        log.info("Received PUT request to update cart item quantity: itemId={}, quantity={}", itemId, request.getQuantity());
        CartResponse response = cartService.updateQuantity(itemId, request.getQuantity());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> removeFromCart(@PathVariable Long itemId) {
        log.info("Received DELETE request to remove item from cart: itemId={}", itemId);
        CartResponse response = cartService.removeFromCart(itemId);
        return ResponseEntity.ok(response);
    }
}
