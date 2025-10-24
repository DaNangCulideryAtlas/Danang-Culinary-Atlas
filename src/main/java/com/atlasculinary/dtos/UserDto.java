package com.atlasculinary.dtos;

import com.atlasculinary.enums.AccountStatus;
import com.atlasculinary.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class UserDto {
    private UUID accountId;
    private String email;
    private String fullName;
    private String avatarUrl;
    private AccountStatus status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private Gender gender;
}
