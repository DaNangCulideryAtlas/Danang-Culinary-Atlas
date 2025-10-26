package com.atlasculinary.dtos;

import com.atlasculinary.enums.ApprovalStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateDishApprovalRequest {

    @NotNull(message = "Approval status is required.")
    private ApprovalStatus approvalStatus;

    private String rejectionReason;
}