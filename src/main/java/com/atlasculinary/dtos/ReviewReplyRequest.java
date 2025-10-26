package com.atlasculinary.dtos;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.UUID;

@Data
public class ReviewReplyRequest {

    @Size(max = 1000, message = "Reply is too long.")
    private String vendorReply;
}