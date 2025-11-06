package com.atlasculinary.services.impl;

import com.atlasculinary.dtos.*;
import com.atlasculinary.entities.Account;
import com.atlasculinary.entities.Restaurant;
import com.atlasculinary.entities.Review;
import com.atlasculinary.exceptions.ResourceNotFoundException;
import com.atlasculinary.mappers.ReviewMapper;
import com.atlasculinary.repositories.RestaurantRepository;
import com.atlasculinary.repositories.ReviewRepository;
import com.atlasculinary.services.AccountService;
import com.atlasculinary.services.RestaurantService;
import com.atlasculinary.services.RestaurantStatsService;
import com.atlasculinary.services.ReviewService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final AccountService accountService;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantStatsService restaurantStatsService;
    private final ReviewMapper reviewMapper;

    @Override
    @Transactional
    public ReviewDto addReview(AddReviewRequest request, UUID userId) {
        Review review = reviewMapper.toEntity(request);
        UUID restaurantId = request.getRestaurantId();
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(()-> new ResourceNotFoundException("Restaurant not found with ID " + restaurantId));

        Account user = accountService.getAccountById(userId);
        review.setRestaurant(restaurant);
        review.setReviewerAccount(user);
        var reviewSaved = reviewRepository.save(review);

        Integer newRating = reviewSaved.getRating();
        // RestaurantStats
        restaurantStatsService.updateStatsOnReviewEvent(
                restaurantId,
                null,
                newRating
        );
        return reviewMapper.toDto(reviewSaved);
    }

    @Override
    @Transactional
    public ReviewDto updateReview(UUID reviewId, UpdateReviewRequest request, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        Integer oldRating = review.getRating();
        UUID restaurantId = review.getRestaurant().getRestaurantId();
        UUID ownerId = review.getReviewerAccount().getAccountId();
        boolean isAdmin = accountService.isAdmin(userId);

        if (!ownerId.equals(userId) && !isAdmin) {
            throw new SecurityException("Bạn không có quyền sửa bình luận này.");
        }

        reviewMapper.updateEntityFromRequest(request, review);
        var reviewUpdated = reviewRepository.save(review);

        Integer newRating = reviewUpdated.getRating();
        // RestaurantStats
        restaurantStatsService.updateStatsOnReviewEvent(
                restaurantId,
                oldRating,
                newRating
        );
        return reviewMapper.toDto(reviewUpdated);
    }

    @Override
    @Transactional
    public void deleteReview(UUID reviewId, UUID userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        UUID restaurantId = review.getRestaurant().getRestaurantId();
        Integer oldRating = review.getRating();
        UUID ownerId = review.getReviewerAccount().getAccountId();
        boolean isAdmin = accountService.isAdmin(userId);

        if (!ownerId.equals(userId) && !isAdmin) {
            throw new SecurityException("Bạn không có quyền sửa bình luận này.");
        }
        // RestaurantStats
        restaurantStatsService.updateStatsOnReviewEvent(
                restaurantId,
                oldRating,
                null
        );
        reviewRepository.delete(review);
    }

    @Override
    public Page<ReviewDto> getReviewsByRestaurant(UUID restId, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewPage = reviewRepository.findByRestaurant_RestaurantId(restId, pageable);

        return reviewPage.map(reviewMapper::toDto);

    }

    @Override
    public Page<ReviewDto> getReviewsByDish(UUID dishId, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewPage = reviewRepository.findByDish_DishId(dishId, pageable);

        return reviewPage.map(reviewMapper::toDto);
    }

    @Override
    public ReviewDto getReviewById(UUID reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(()-> new ResourceNotFoundException("Review not found with ID: " + reviewId));

        return reviewMapper.toDto(review);
    }
}
