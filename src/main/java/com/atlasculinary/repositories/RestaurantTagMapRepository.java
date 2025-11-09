package com.atlasculinary.repositories;

import com.atlasculinary.entities.RestaurantTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RestaurantTagMapRepository extends JpaRepository<RestaurantTagMap, Long> {
    void deleteByRestaurantId(UUID restaurantId);

    List<RestaurantTagMap> findByRestaurantId(UUID restaurantId);
}
