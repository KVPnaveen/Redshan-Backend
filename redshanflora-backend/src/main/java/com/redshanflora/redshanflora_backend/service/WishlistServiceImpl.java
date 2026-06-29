package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.WishlistRequest;
import com.redshanflora.redshanflora_backend.dto.WishlistResponse;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.entity.Wishlist;
import com.redshanflora.redshanflora_backend.exception.DuplicateWishlistItemException;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public WishlistResponse addToWishlist(WishlistRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        if (wishlistRepository.existsByUser_IdAndProduct_Id(request.getUserId(), request.getProductId())) {
            throw new DuplicateWishlistItemException("Product already exists in wishlist");
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        Wishlist saved = wishlistRepository.save(wishlist);
        return mapToResponse(saved);
    }

    @Override
    public List<WishlistResponse> getWishlistByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return wishlistRepository.findByUser_Id(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void removeFromWishlist(Long wishlistId) {
        if (!wishlistRepository.existsById(wishlistId)) {
            throw new ResourceNotFoundException("Wishlist item not found with id: " + wishlistId);
        }
        wishlistRepository.deleteById(wishlistId);
    }

    @Override
    public long getWishlistCount(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        return wishlistRepository.countByUser_Id(userId);
    }

    private WishlistResponse mapToResponse(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        return WishlistResponse.builder()
                .wishlistId(wishlist.getId())
                .productId(product.getId())
                .productName(product.getProductName())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .discountPercentage(product.getDiscountPercentage())
                .build();
    }
}
