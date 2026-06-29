package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.WishlistRequest;
import com.redshanflora.redshanflora_backend.dto.WishlistResponse;
import java.util.List;

public interface WishlistService {
    WishlistResponse addToWishlist(WishlistRequest request);
    List<WishlistResponse> getWishlistByUserId(Long userId);
    void removeFromWishlist(Long wishlistId);
    long getWishlistCount(Long userId);
}
