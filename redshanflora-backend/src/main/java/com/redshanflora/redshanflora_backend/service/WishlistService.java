package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.wishlist.WishlistRequest;
import com.redshanflora.redshanflora_backend.dto.wishlist.WishlistResponse;
import java.util.List;

public interface WishlistService {
    WishlistResponse addToWishlist(WishlistRequest request);
    List<WishlistResponse> getWishlistByUserId(Long userId);
    void removeFromWishlist(Long wishlistId);
    long getWishlistCount(Long userId);
}
