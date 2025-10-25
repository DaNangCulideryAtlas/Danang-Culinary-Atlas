package com.atlasculinary.repositories;

import com.atlasculinary.entities.Dish;
import com.atlasculinary.enums.ApprovalStatus;
import com.atlasculinary.enums.DishStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DishRepository extends JpaRepository<Dish, UUID> {
    Page<Dish> findByRestaurant_RestaurantId(UUID restaurantId, Pageable pageable);

    Page<Dish> findByRestaurant_RestaurantIdAndStatusAndApprovalStatus(
            UUID restaurantId,
            DishStatus status,
            ApprovalStatus approvalStatus,
            Pageable pageable
    );

    Page<Dish> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
}
