package com.atlasculinary.mappers;

import com.atlasculinary.dtos.RestaurantTagDto;
import com.atlasculinary.entities.RestaurantTag;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RestaurantTagMapper {
    RestaurantTagDto toDto(RestaurantTag restaurantTag);
    List<RestaurantTagDto> toDtoList(List<RestaurantTag> restaurantTagList);
}
