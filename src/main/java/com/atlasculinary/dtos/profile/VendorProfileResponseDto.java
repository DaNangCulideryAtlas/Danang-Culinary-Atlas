package com.atlasculinary.dtos.profile;

import com.atlasculinary.enums.Gender;
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
public class VendorProfileResponseDto {
  private UUID vendorId;
  private String email;
  private String fullName;
  private String avatarUrl;
  private String phone;
  private String description;
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dob;
  private Gender gender;

//  private String businessLicenseNumber;
//  private String businessLicenseStatus;
}