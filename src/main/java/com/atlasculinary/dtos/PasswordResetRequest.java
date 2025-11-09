package com.atlasculinary.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PasswordResetRequest {
    private UUID accountId;
    private String resetToken;
    private String platform;
}
