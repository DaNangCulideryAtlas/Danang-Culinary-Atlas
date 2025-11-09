package com.atlasculinary.dtos;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateRestaurantRequest {

    private String name;

    private String address;

    private Map<String, Object> images;

    private Integer wardId;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private List<Long> tagIds;

    private Map<String, String> openingHours;

}