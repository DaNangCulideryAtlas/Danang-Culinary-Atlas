package com.atlasculinary.repositories;

import com.atlasculinary.entities.RestaurantStats;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantStatsRepository extends JpaRepository<RestaurantStats, UUID> {
}
