package com.atlasculinary.controllers;

import com.atlasculinary.dtos.*;
import com.atlasculinary.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "API for authentication and password management")
public class AuthController {

  private final AuthService authService;

  @Operation(summary = "Đăng ký tài khoản mới")
  @PostMapping("/signup")
  public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
    authService.signUp(signUpRequest);
    ApiResponse response = ApiResponse.success("Đăng ký tài khoản thành công");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Đăng nhập")
  @PostMapping("/login")
  public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    LoginResponse loginResponse = authService.login(loginRequest);
    ApiResponse response = ApiResponse.success("Đăng nhập thành công", loginResponse);
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Thay đổi mật khẩu (yêu cầu đăng nhập)")
  @PutMapping("/change-password")
  public ResponseEntity<ApiResponse> changePassword(
      @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
      Authentication authentication) {
    String email = authentication.getName();
    authService.changePassword(email, changePasswordRequest);
    ApiResponse response = ApiResponse.success("Thay đổi mật khẩu thành công");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Quên mật khẩu - Gửi email reset")
  @PostMapping("/forgot-password")
  public ResponseEntity<ApiResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest forgotPasswordRequest) {
    authService.forgotPassword(forgotPasswordRequest);
    ApiResponse response = ApiResponse.success("Đã gửi email hướng dẫn đặt lại mật khẩu. Vui lòng kiểm tra hộp thư của bạn.");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Đặt lại mật khẩu với token")
  @PostMapping("/reset-password")
  public ResponseEntity<ApiResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
    authService.resetPassword(resetPasswordRequest);
    ApiResponse response = ApiResponse.success("Đặt lại mật khẩu thành công");
    return ResponseEntity.ok(response);
  }

  @Operation(summary = "Kiểm tra tính hợp lệ của token reset password")
  @GetMapping("/validate-reset-token")
  public ResponseEntity<ApiResponse> validateResetToken(@RequestParam String token) {
    boolean isValid = authService.validateResetToken(token);
    if (isValid) {
      ApiResponse response = ApiResponse.success("Token hợp lệ");
      return ResponseEntity.ok(response);
    } else {
      ApiResponse response = ApiResponse.error("Token không hợp lệ hoặc đã hết hạn");
      return ResponseEntity.badRequest().body(response);
    }
  }
}
