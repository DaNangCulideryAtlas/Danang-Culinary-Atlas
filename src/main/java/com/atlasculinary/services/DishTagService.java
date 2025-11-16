package com.atlasculinary.services;

import com.atlasculinary.dtos.DishTagDto;

import java.util.List;
import java.util.UUID;

public interface DishTagService {
    List<DishTagDto> getAllDishTag();
    void deleteDishTagsByDishId(UUID dishId);
}
