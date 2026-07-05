package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.cart.AddToCartRequest;
import com.redshanflora.redshanflora_backend.dto.cart.CartItemDto;
import com.redshanflora.redshanflora_backend.dto.cart.CartResponse;
import com.redshanflora.redshanflora_backend.entity.Cart;
import com.redshanflora.redshanflora_backend.entity.CartItem;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.exception.InvalidCredentialsException;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.CartItemRepository;
import com.redshanflora.redshanflora_backend.repository.CartRepository;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private User resolveCurrentUser() {
        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()
                || auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            throw new InvalidCredentialsException("Authentication required");
        }

        String email = auth.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    @Override
    @Transactional
    public CartResponse addToCart(AddToCartRequest request) {
        User user = resolveCurrentUser();
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        // Product availability/status is not currently represented in Product.java.
        // Stock quantity validation is intentionally deferred for Phase 1.

        // Resolve or create Cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .cartItems(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });

        // Normalize color
        String color = request.getSelectedColor();
        if (color == null || color.trim().isEmpty()) {
            color = "Default";
        }

        // Check if item already exists
        Optional<CartItem> existingItemOpt = cartItemRepository.findByCart_IdAndProduct_IdAndSelectedColor(
                cart.getId(), product.getId(), color);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + request.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.getQuantity())
                    .selectedColor(color)
                    .build();

            // Add to the managed cart items collection
            cart.getCartItems().add(newItem);
            // Save the Cart once (cascades save to newItem via CascadeType.ALL)
            cartRepository.save(cart);
        }

        return buildCartResponse(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getCurrentUserCart() {
        User user = resolveCurrentUser();
        Optional<Cart> cartOpt = cartRepository.findByUser(user);
        if (cartOpt.isEmpty()) {
            return CartResponse.builder()
                    .items(new ArrayList<>())
                    .totalItems(0)
                    .subtotal(BigDecimal.ZERO)
                    .build();
        }
        return buildCartResponse(cartOpt.get());
    }

    @Override
    @Transactional
    public CartResponse updateQuantity(Long itemId, Integer quantity) {
        User user = resolveCurrentUser();

        // Validate quantity
        if (quantity == null || quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero");
        }

        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Verify ownership
        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);

        return buildCartResponse(cartItem.getCart());
    }

    @Override
    @Transactional
    public CartResponse removeFromCart(Long itemId) {
        User user = resolveCurrentUser();
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        // Verify ownership
        if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
            throw new ResourceNotFoundException("Cart item not found");
        }

        Cart cart = cartItem.getCart();

        // Remove from managed cart collection (cascades delete via orphanRemoval =
        // true)
        cart.getCartItems().remove(cartItem);

        return buildCartResponse(cart);
    }

    private CartResponse buildCartResponse(Cart cart) {
        List<CartItem> items = cart.getCartItems();
        if (items == null) {
            items = new ArrayList<>();
        }

        List<CartItemDto> itemDtos = items.stream().map(item -> {
            Product p = item.getProduct();
            return new CartItemDto(
                    item.getId(), // id = cart item ID
                    p.getId(), // productId = product ID
                    p.getProductName(),
                    item.getQuantity(),
                    false, // isCustom = false
                    p.getPrice().doubleValue(), // numericPrice = product price
                    null, // bouquetDesign = null (Phase 1)
                    item.getSelectedColor() // selectedColor = cart item color
            );
        }).collect(Collectors.toList());

        int totalItems = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        BigDecimal subtotal = items.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return CartResponse.builder()
                .items(itemDtos)
                .totalItems(totalItems)
                .subtotal(subtotal)
                .build();
    }
}
