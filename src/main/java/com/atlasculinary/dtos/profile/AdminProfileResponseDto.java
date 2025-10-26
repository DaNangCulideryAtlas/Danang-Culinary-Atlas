package com.atlasculinary.dtos.profile;

import com.atlasculinary.enums.Gender;
import com.atlasculinary.enums.RoleLevel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProfileResponseDto {
  private UUID adminId;
  private String email;
  private String fullName;
  private String avatarUrl;
  private String phone;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dob;

  private Gender gender;
  private RoleLevel roleLevel;
}