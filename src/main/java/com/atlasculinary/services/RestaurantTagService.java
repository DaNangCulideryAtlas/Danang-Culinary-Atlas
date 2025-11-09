package com.atlasculinary.services;

import com.atlasculinary.dtos.RestaurantTagDto;

import java.util.List;
import java.util.UUID;

public interface RestaurantTagService {
    List<RestaurantTagDto> getAllRestaurantTag();
    void addTagsToRestaurant(UUID restaurantId, List<Long> tagIds);
    void updateTagsForRestaurant(UUID restaurantId, List<Long> tagIds);
    List<RestaurantTagDto> getRestaurantTagsByRestaurantId(UUID restaurantId);
}
