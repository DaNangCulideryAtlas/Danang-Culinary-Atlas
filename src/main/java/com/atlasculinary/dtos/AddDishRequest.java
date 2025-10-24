package com.atlasculinary.dtos;

import com.atlasculinary.enums.ApprovalStatus;
import com.atlasculinary.enums.DishStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AddDishRequest {
    @NotNull(message = "RestaurantId is required")
    private UUID restaurantId;

    @NotBlank(message = "Name of dish is not empty.")
    @Size(max = 255, message = "Name of dish is too long")
    private String name;

    private String[] images;

    @NotBlank(message = "Description is not empty.")
    private String description;

    @NotNull(message = "Price is not empty.")
    @DecimalMin(value = "0.01", message = "Price is strict greater than zero.")
    private BigDecimal price;
    private DishStatus status = DishStatus.AVAILABLE;
}
