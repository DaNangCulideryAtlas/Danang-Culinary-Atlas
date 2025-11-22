package com.atlasculinary.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RestaurantMapViewDto {
    private UUID restaurantId;
    private String name;
    private String address;
    private String photo;
    private int wardId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal averageRating;
    private Integer totalReviews;
}
