package com.atlasculinary.services.impl;

import com.atlasculinary.dtos.ReviewDto;
import com.atlasculinary.dtos.ReviewReplyRequest;
import com.atlasculinary.entities.Account;
import com.atlasculinary.entities.Review;
import com.atlasculinary.exceptions.ResourceNotFoundException;
import com.atlasculinary.mappers.ReviewMapper;
import com.atlasculinary.repositories.ReviewRepository;
import com.atlasculinary.services.AccountService;
import com.atlasculinary.services.VendorReviewService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class VendorReviewServiceImpl implements VendorReviewService {
    private final AccountService accountService;
    private final ReviewMapper reviewMapper;
    private final ReviewRepository reviewRepository;

    @Override
    public ReviewDto replyToReview(UUID reviewId, ReviewReplyRequest request, UUID vendorId) {
        Account accountReply =accountService.getAccountById(vendorId);

        var review = reviewRepository.findById(reviewId)
                        .orElseThrow(()-> new ResourceNotFoundException("Review not found with ID: " + reviewId));
        reviewMapper.updateReply(request, review);

        var reviewSaved = reviewRepository.save(review);
        return reviewMapper.toDto(reviewSaved);

    }

//    @Override
//    public ReportDto reportReview(ReviewReportRequest request, UUID vendorId) {
//        return null;
//    }

    @Override
    public Page<ReviewDto> getVendorReviews(UUID restaurantId, int page, int size, String sortBy, String sortDirection) {
        Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortBy);

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Review> reviewPage = reviewRepository.findByRestaurant_RestaurantId(restaurantId, pageable);

        return reviewPage.map(reviewMapper::toDto);

    }
}
