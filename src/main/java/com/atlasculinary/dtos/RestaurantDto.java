package com.atlasculinary.dtos;
import com.atlasculinary.enums.ApprovalStatus;
import com.atlasculinary.enums.RestaurantStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class RestaurantDto {
    private UUID restaurantId;

    private UUID ownerAccountId;

    private String name;

    private String address;

    private Map<String, Object> images;

    private int wardId;

    private RestaurantStatus status = RestaurantStatus.ACTIVE;

    private LocalDateTime createdAt;

    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private UUID approvedByAccountId;

    private LocalDateTime approvedAt;

    private String rejectionReason;

    private List<RestaurantTagDto> tags;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Map<String, Object> openingHours;

    private BigDecimal averageRating = BigDecimal.ZERO;

    private Integer totalReviews = 0;
}
