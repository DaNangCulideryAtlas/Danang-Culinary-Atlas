package com.atlasculinary.dtos;

import java.util.UUID;

public class ReportRequest {
    private UUID restaurantId;
    private UUID dishId;
    private UUID reviewId;
    private String reason;

    public UUID getRestaurantId() { return restaurantId; }
    public void setRestaurantId(UUID restaurantId) { this.restaurantId = restaurantId; }
    public UUID getDishId() { return dishId; }
    public void setDishId(UUID dishId) { this.dishId = dishId; }
    public UUID getReviewId() { return reviewId; }
    public void setReviewId(UUID reviewId) { this.reviewId = reviewId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
