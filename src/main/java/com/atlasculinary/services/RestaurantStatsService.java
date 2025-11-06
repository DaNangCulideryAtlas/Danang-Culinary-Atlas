package com.atlasculinary.services;

import java.math.BigDecimal;
import java.util.UUID;

public interface RestaurantStatsService {
    void updateStatsOnReviewEvent(UUID restaurantId, Integer oldRating, Integer newRating);
}
