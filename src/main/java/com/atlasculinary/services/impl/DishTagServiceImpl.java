package com.atlasculinary.services.impl;

import com.atlasculinary.dtos.DishTagDto;
import com.atlasculinary.entities.DishTag;
import com.atlasculinary.mappers.DishTagMapper;
import com.atlasculinary.repositories.DishTagRepository;
import com.atlasculinary.services.DishTagService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DishTagServiceImpl implements DishTagService {
    private final DishTagRepository dishTagRepository;
    private final DishTagMapper dishTagMapper;
    @Override
    public List<DishTagDto> getAllDishTag() {
        List<DishTag> dishTagList = dishTagRepository.findAll();
        return dishTagMapper.toDtoList(dishTagList);
    }
}
