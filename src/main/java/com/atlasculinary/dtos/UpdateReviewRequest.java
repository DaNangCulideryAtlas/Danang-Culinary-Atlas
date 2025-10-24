package com.atlasculinary.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class UpdateReviewRequest {

    @Min(value = 1, message = "Min rating is 1")
    @Max(value = 5, message = "Max rating is 5")
    private Integer rating;

    @Size(max = 1000, message = "Comment is too long.")
    private String comment;

    private String[] images;
}