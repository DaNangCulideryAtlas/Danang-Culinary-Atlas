package com.atlasculinary.controllers;

import com.atlasculinary.dtos.DishTagDto;
import com.atlasculinary.dtos.RestaurantTagDto;
import com.atlasculinary.services.DishTagService;
import com.atlasculinary.services.RestaurantTagService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tags")
@AllArgsConstructor
public class TagController {

    private final RestaurantTagService restaurantTagService;
    private final DishTagService dishTagService;
    @GetMapping("/restaurant")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    public ResponseEntity<List<RestaurantTagDto>> getAllRestaurantTags() {
        List<RestaurantTagDto> tags = restaurantTagService.getAllRestaurantTag();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/dish")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    public ResponseEntity<List<DishTagDto>> getAllDishTags() {
        List<DishTagDto> tags = dishTagService.getAllDishTag();
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAnyAuthority('VENDOR', 'ADMIN')")
    public ResponseEntity<List<RestaurantTagDto>> getRestaurantTagsByRestaurantId(@PathVariable UUID restaurantId) {

        List<RestaurantTagDto> tags = restaurantTagService.getRestaurantTagsByRestaurantId(restaurantId);
        if (tags.isEmpty()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(tags);
    }
}