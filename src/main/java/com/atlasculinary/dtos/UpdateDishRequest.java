package com.atlasculinary.dtos;

import com.atlasculinary.enums.DishStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class UpdateDishRequest {

    @Size(max = 255, message = "Name of dish is too long")
    private String name;

    private String[] images;

    private String description;

    @DecimalMin(value = "0.01", message = "Price is strict greater than zero.")
    private BigDecimal price;

    private DishStatus status;
}
