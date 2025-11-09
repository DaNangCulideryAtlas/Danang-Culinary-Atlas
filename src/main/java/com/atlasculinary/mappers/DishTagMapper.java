package com.atlasculinary.mappers;

import com.atlasculinary.dtos.DishTagDto;
import com.atlasculinary.entities.DishTag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DishTagMapper {
    DishTagDto toDto(DishTag dishTag);
    List<DishTagDto> toDtoList(List<DishTag> dishTagDtoList);
}
