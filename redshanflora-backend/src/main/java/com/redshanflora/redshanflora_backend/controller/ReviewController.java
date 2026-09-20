package com.redshanflora.redshanflora_backend.controller;

import com.redshanflora.redshanflora_backend.dto.review.ReviewRequest;
import com.redshanflora.redshanflora_backend.dto.review.ReviewResponse;
import com.redshanflora.redshanflora_backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/reviews")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProductId(@PathVariable Long productId) {
        log.info("Received GET request to fetch reviews for productId={}", productId);
        List<ReviewResponse> reviews = reviewService.getReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }

    @GetMapping("/{productId}/average")
    public ResponseEntity<Map<String, Object>> getAverageRating(@PathVariable Long productId) {
        log.info("Received GET request to calculate average rating for productId={}", productId);
        Double average = reviewService.getAverageRatingByProductId(productId);
        return ResponseEntity.ok(Map.of("productId", productId, "averageRating", average));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createOrUpdateReview(@Valid @RequestBody ReviewRequest request) {
        log.info("Received POST request to submit review for productId={}, userId={}", request.getProductId(), request.getUserId());
        ReviewResponse response = reviewService.createOrUpdateReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Review saved successfully",
                "data", response
        ));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Map<String, String>> deleteReview(@PathVariable Long reviewId) {
        log.info("Received DELETE request for reviewId={}", reviewId);
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(Map.of("message", "Review deleted successfully"));
    }
}
