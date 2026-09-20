package com.redshanflora.redshanflora_backend.service.impl;

import com.redshanflora.redshanflora_backend.dto.review.ReviewRequest;
import com.redshanflora.redshanflora_backend.dto.review.ReviewResponse;
import com.redshanflora.redshanflora_backend.entity.Customer;
import com.redshanflora.redshanflora_backend.entity.Product;
import com.redshanflora.redshanflora_backend.entity.Review;
import com.redshanflora.redshanflora_backend.entity.User;
import com.redshanflora.redshanflora_backend.exception.ResourceNotFoundException;
import com.redshanflora.redshanflora_backend.repository.CustomerRepository;
import com.redshanflora.redshanflora_backend.repository.CustomerReviewRepository;
import com.redshanflora.redshanflora_backend.repository.ProductRepository;
import com.redshanflora.redshanflora_backend.repository.UserRepository;
import com.redshanflora.redshanflora_backend.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final CustomerReviewRepository reviewRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private User resolveCurrentUser(Long explicitUserId) {
        if (explicitUserId != null) {
            return userRepository.findById(explicitUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + explicitUserId));
        }

        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new ResourceNotFoundException("Authentication required to post a review");
        }

        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    private Customer getOrCreateCustomer(User user) {
        return customerRepository.findByUser(user)
                .orElseGet(() -> {
                    Customer newCustomer = Customer.builder()
                            .user(user)
                            .build();
                    return customerRepository.save(newCustomer);
                });
    }

    @Override
    @Transactional
    public ReviewResponse createOrUpdateReview(ReviewRequest request) {
        User user = resolveCurrentUser(request.getUserId());
        Customer customer = getOrCreateCustomer(user);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + request.getProductId()));

        Optional<Review> existingReviewOpt = reviewRepository.findByCustomer_IdAndProduct_Id(customer.getId(), product.getId());

        Review review;
        if (existingReviewOpt.isPresent()) {
            review = existingReviewOpt.get();
            review.setRating(request.getRating());
            review.setComment(request.getComment());
            review.setReviewDate(Instant.now());
            log.info("Updating existing review ID={} for customerId={} on productId={}", review.getId(), customer.getId(), product.getId());
        } else {
            review = Review.builder()
                    .customer(customer)
                    .product(product)
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .reviewDate(Instant.now())
                    .build();
            log.info("Creating new review for customerId={} on productId={}", customer.getId(), product.getId());
        }

        Review savedReview = reviewRepository.save(review);
        return mapToResponse(savedReview);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        List<Review> reviews = reviewRepository.findByProduct_IdOrderByReviewDateDesc(productId);
        return reviews.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByProductId(Long productId) {
        Double avg = reviewRepository.findAverageRatingByProductId(productId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0;
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        reviewRepository.delete(review);
        log.info("Deleted review ID={}", reviewId);
    }

    private ReviewResponse mapToResponse(Review review) {
        Customer customer = review.getCustomer();
        User user = customer != null ? customer.getUser() : null;

        return ReviewResponse.builder()
                .reviewId(review.getId())
                .customerId(customer != null ? customer.getId() : null)
                .userId(user != null ? user.getId() : null)
                .customerName(user != null ? user.getName() : "Anonymous Customer")
                .productId(review.getProduct().getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .build();
    }
}
