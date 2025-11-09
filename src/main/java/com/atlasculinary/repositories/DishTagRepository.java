package com.atlasculinary.repositories;

import com.atlasculinary.entities.DishTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DishTagRepository extends JpaRepository<DishTag, Long> {
}
