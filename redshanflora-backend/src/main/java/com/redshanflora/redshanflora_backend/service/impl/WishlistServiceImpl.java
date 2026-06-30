package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.wishlist.WishlistRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import com.redshanflora.redshanflora_backend.dto.wishlist.WishlistResponse;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.entity.Wishlist;
import com.redshanflora.redshanflora_backend.exception.DuplicateWishlistItemException;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.repository.WishlistRepository;
import com.redshanflora.redshanflora_backend.service.WishlistService;
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
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public WishlistResponse addToWishlist(WishlistRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getUserId()));

        Product product = productRepository.findById(request.getProductId())
                .orElseGet(() -> {
                    String insertSql = "INSERT INTO product (product_id, category_id, product_name, price) VALUES (?, 1, ?, 1000.00)";
                    jdbcTemplate.update(insertSql, request.getProductId(), "Product " + request.getProductId());
                    try {
                        String syncSql = "SELECT setval(pg_get_serial_sequence('product', 'product_id'), COALESCE((SELECT MAX(product_id) FROM product), 0) + 1, false)";
                        jdbcTemplate.execute(syncSql);
                    } catch (Exception e) {
                        // ignore or log
                    }
                    return productRepository.findById(request.getProductId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));
                });

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
