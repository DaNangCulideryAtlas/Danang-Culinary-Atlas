package com.atlasculinary.mappers;

import com.atlasculinary.dtos.AddRestaurantRequest;
import com.atlasculinary.dtos.RestaurantDto;
import com.atlasculinary.dtos.RestaurantMapViewDto;
import com.atlasculinary.dtos.RestaurantTagDto;
import com.atlasculinary.dtos.UpdateRestaurantRequest;
import com.atlasculinary.entities.Restaurant;
import com.atlasculinary.entities.RestaurantTagMap;
import lombok.AllArgsConstructor;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel="spring", uses = {RestaurantTagMapper.class})
public interface RestaurantMapper {
    @Mapping(source = "ownerAccount.accountId", target = "ownerAccountId")
    @Mapping(source = "ward.wardId", target = "wardId")
    @Mapping(source = "approvedByAccount.accountId", target = "approvedByAccountId")
    @Mapping(source = "restaurantStats.averageRating", target = "averageRating")
    @Mapping(source = "restaurantStats.totalReviews", target = "totalReviews")
//    @Mapping(target = "tags", expression = "java(entity.getTagDtos())")
    RestaurantDto toDto(Restaurant entity);
    @Mapping(target = "photo", expression = "java(extractPhoto(entity))")
    RestaurantMapViewDto toMapViewDto(Restaurant entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateRestaurantFromRequest(
            UpdateRestaurantRequest request,
            @MappingTarget Restaurant targetEntity
    );

    Restaurant toEntity(AddRestaurantRequest request);

    List<RestaurantDto> toDtoList(List<Restaurant> restaurantList);

    List<RestaurantMapViewDto> toMapViewDtoList(List<Restaurant> restaurantList);

    default String extractPhoto(Restaurant restaurant) {
        if (restaurant.getImages() != null && restaurant.getImages().containsKey("photo")) {
            Object photoObj = restaurant.getImages().get("photo");
            return photoObj != null ? photoObj.toString() : null;
        }
        return null;
    }

}
