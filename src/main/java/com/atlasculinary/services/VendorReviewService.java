package com.atlasculinary.services;

import com.atlasculinary.dtos.ReviewDto;
import com.atlasculinary.dtos.ReviewReplyRequest;
import com.atlasculinary.dtos.ReviewReportRequest;
import com.atlasculinary.dtos.ReportDto;
import org.springframework.data.domain.Page;
import java.util.UUID;

public interface VendorReviewService {

    ReviewDto replyToReview(UUID reviewId, ReviewReplyRequest request, UUID vendorId);

//    ReportDto reportReview(ReviewReportRequest request, UUID vendorId);

    Page<ReviewDto> getVendorReviews(UUID restaurantId, int page, int size, String sortBy, String sortDirection);
}