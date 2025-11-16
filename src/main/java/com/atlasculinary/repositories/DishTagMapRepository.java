package com.atlasculinary.repositories;

import com.atlasculinary.entities.DishTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DishTagMapRepository extends JpaRepository<DishTagMap, Long> {
    void deleteByDishId(UUID dishId);
}
