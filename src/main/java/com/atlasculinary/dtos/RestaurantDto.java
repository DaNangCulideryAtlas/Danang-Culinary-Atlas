package com.atlasculinary.dtos;
import com.atlasculinary.enums.ApprovalStatus;
import com.atlasculinary.enums.RestaurantStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
public class RestaurantDto {
    private UUID restaurantId;

    private UUID ownerAccountId;

    private String name;

    private String address;

    private String[] images;

    private int wardId;

    private RestaurantStatus status = RestaurantStatus.ACTIVE;

    private LocalDateTime createdAt;

    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private UUID approvedByAccountId;

    private LocalDateTime approvedAt;

    private String rejectionReason;

    private BigDecimal latitude;

    private BigDecimal longitude;

    private Map<String, Object> openingHours;
}
