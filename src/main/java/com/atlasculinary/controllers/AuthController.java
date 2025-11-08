package com.atlasculinary.controllers;

import com.atlasculinary.dtos.ApiResponse;
import com.atlasculinary.dtos.ChangePasswordRequest;
import com.atlasculinary.dtos.LoginRequest;
import com.atlasculinary.dtos.LoginResponse;
import com.atlasculinary.dtos.SignUpRequest;
import com.atlasculinary.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
    authService.signUp(signUpRequest);
    ApiResponse response = ApiResponse.success("Đăng ký tài khoản thành công");
    return ResponseEntity.ok(response);
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    LoginResponse loginResponse = authService.login(loginRequest);
    ApiResponse response = ApiResponse.success("Đăng nhập thành công", loginResponse);
    return ResponseEntity.ok(response);
  }

  @PutMapping("/change-password")
  public ResponseEntity<ApiResponse> changePassword(
      @Valid @RequestBody ChangePasswordRequest changePasswordRequest,
      Authentication authentication) {
    String email = authentication.getName();
    authService.changePassword(email, changePasswordRequest);
    ApiResponse response = ApiResponse.success("Thay đổi mật khẩu thành công");
    return ResponseEntity.ok(response);
  }
}
