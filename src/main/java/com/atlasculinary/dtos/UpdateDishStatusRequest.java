package com.atlasculinary.dtos;

import com.atlasculinary.enums.DishStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDishStatusRequest {
    @NotNull(message = "Dish status is required")
    private DishStatus status = DishStatus.AVAILABLE;

}
