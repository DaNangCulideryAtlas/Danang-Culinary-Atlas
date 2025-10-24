package com.atlasculinary.controllers;

import com.atlasculinary.dtos.AdminDto;
import com.atlasculinary.dtos.ApiResponse;
import com.atlasculinary.dtos.UserDto;
import com.atlasculinary.dtos.VendorDto;
import com.atlasculinary.dtos.profile.*;
import com.atlasculinary.services.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

  private final ProfileService profileService;

  @GetMapping("/user")
  @PreAuthorize("hasAuthority('USER')")
  public ResponseEntity<ApiResponse> getUserProfile(Authentication authentication) {
    String email = authentication.getName();
    UserDto profile = profileService.getUserProfile(email);
    return ResponseEntity.ok(ApiResponse.success("Lấy thông tin người dùng thành công", profile));
  }

  @PutMapping("/user")
  @PreAuthorize("hasAuthority('USER')")
  public ResponseEntity<ApiResponse> updateUserProfile(
        @Valid @RequestBody UserProfileUpdateDto updateDto,
        Authentication authentication) {
    String email = authentication.getName();
    UserDto profile = profileService.updateUserProfile(email, updateDto);
    return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin người dùng thành công", profile));
  }

  @GetMapping("/admin")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse> getAdminProfile(Authentication authentication) {
    String email = authentication.getName();
    AdminDto profile = profileService.getAdminProfile(email);
    return ResponseEntity.ok(ApiResponse.success("Lấy thông tin quản trị viên thành công", profile));
  }

  @PutMapping("/admin")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<ApiResponse> updateAdminProfile(
        @Valid @RequestBody AdminProfileUpdateDto updateDto,
        Authentication authentication) {
    String email = authentication.getName();
    AdminDto profile = profileService.updateAdminProfile(email, updateDto);
    return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin quản trị viên thành công", profile));
  }

  @GetMapping("/vendor")
  @PreAuthorize("hasAuthority('VENDOR')")
  public ResponseEntity<ApiResponse> getVendorProfile(Authentication authentication) {
    String email = authentication.getName();
    VendorDto profile = profileService.getVendorProfile(email);
    return ResponseEntity.ok(ApiResponse.success("Lấy thông tin nhà cung cấp thành công", profile));
  }

  @PutMapping("/vendor")
  @PreAuthorize("hasAuthority('VENDOR')")
  public ResponseEntity<ApiResponse> updateVendorProfile(
        @Valid @RequestBody VendorProfileUpdateDto updateDto,
        Authentication authentication) {
    String email = authentication.getName();
    VendorDto profile = profileService.updateVendorProfile(email, updateDto);
    return ResponseEntity.ok(ApiResponse.success("Cập nhật thông tin nhà cung cấp thành công", profile));
  }
}