package com.atlasculinary.services;

import com.atlasculinary.dtos.AddRestaurantRequest;
import com.atlasculinary.dtos.RestaurantDto;
import com.atlasculinary.dtos.UpdateApprovalStatusRequest;
import com.atlasculinary.dtos.UpdateRestaurantRequest;
import com.atlasculinary.enums.ApprovalStatus;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface RestaurantService {

    RestaurantDto createRestaurant(UUID ownerAccountId, AddRestaurantRequest request);


    RestaurantDto getRestaurantById(UUID restaurantId);


    Page<RestaurantDto> getAllRestaurants(int page, int size, String sortBy, String sortDirection);

    Page<RestaurantDto> getAllRestaurantsByVendor(UUID vendorId, int page, int size, String sortBy, String sortDirection);


    RestaurantDto updateRestaurant(UUID restaurantId, UpdateRestaurantRequest request, UUID accessAccountId);


    void deleteRestaurant(UUID restaurantId, UUID accessAccountId);


    RestaurantDto updateApprovalStatus(
            UUID adminAccountId,
            UUID restaurantId,
            UpdateApprovalStatusRequest request
    );

    Page<RestaurantDto> getAllRestaurantsApproved(int page, int size, String sortBy, String sortDirection);
}
