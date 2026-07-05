package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.cart.AddToCartRequest;
import com.redshanflora.redshanflora_backend.dto.cart.CartResponse;

public interface CartService {
    CartResponse addToCart(AddToCartRequest request);
    CartResponse getCurrentUserCart();
    CartResponse updateQuantity(Long itemId, Integer quantity);
    CartResponse removeFromCart(Long itemId);
}
