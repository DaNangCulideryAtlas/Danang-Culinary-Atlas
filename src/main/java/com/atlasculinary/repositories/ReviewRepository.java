package com.atlasculinary.repositories;

import com.atlasculinary.entities.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReviewRepository extends JpaRepository<Review, UUID> {
    Page<Review> findByRestaurant_RestaurantId(UUID restId, Pageable pageable);

    Page<Review> findByDish_DishId(UUID dishId, Pageable pageable);
}
