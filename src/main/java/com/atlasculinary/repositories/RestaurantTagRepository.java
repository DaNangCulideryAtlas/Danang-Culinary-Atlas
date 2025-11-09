package com.atlasculinary.repositories;

import com.atlasculinary.entities.RestaurantTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface RestaurantTagRepository extends JpaRepository<RestaurantTag, Long> {

}
