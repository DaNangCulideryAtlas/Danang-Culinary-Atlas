package com.atlasculinary.dtos;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReviewDto {
    private UUID reviewId;
    private UUID reviewerAccountId;
    private String reviewerUsername;
    private UUID restaurantId;
    private UUID dishId;
    private Integer rating;
    private String comment;
    private String[] images;
    private LocalDateTime createdAt;
    private String vendorReply;
    private LocalDateTime repliedAt;
    private boolean hasOpenReport;
}