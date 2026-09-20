package com.redshanflora.redshanflora_backend.service;

import com.redshanflora.redshanflora_backend.dto.review.ReviewRequest;
import com.redshanflora.redshanflora_backend.dto.review.ReviewResponse;
import java.util.List;

public interface ReviewService {

    ReviewResponse createOrUpdateReview(ReviewRequest request);

    List<ReviewResponse> getReviewsByProductId(Long productId);

    Double getAverageRatingByProductId(Long productId);

    void deleteReview(Long reviewId);
}
