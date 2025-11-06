package com.atlasculinary.mappers;

import com.atlasculinary.dtos.AddRestaurantRequest;
import com.atlasculinary.dtos.RestaurantDto;
import com.atlasculinary.dtos.UpdateRestaurantRequest;
import com.atlasculinary.entities.Restaurant;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel="spring")
public interface RestaurantMapper {

    @Mapping(source = "ownerAccount.accountId", target = "ownerAccountId")
    @Mapping(source = "ward.wardId", target = "wardId")
    @Mapping(source = "approvedByAccount.accountId", target = "approvedByAccountId")
    @Mapping(source = "restaurantStats.averageRating", target = "averageRating")
    @Mapping(source = "restaurantStats.totalReviews", target = "totalReviews")
    RestaurantDto toDto(Restaurant entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRestaurantFromRequest(
            UpdateRestaurantRequest request,
            @MappingTarget Restaurant targetEntity
    );

    Restaurant toEntity(AddRestaurantRequest request);

    List<RestaurantDto> toDtoList(List<Restaurant> restaurantList);
}
