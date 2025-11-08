package com.atlasculinary.services;

import com.atlasculinary.dtos.ChangePasswordRequest;
import com.atlasculinary.dtos.LoginRequest;
import com.atlasculinary.dtos.LoginResponse;
import com.atlasculinary.dtos.SignUpRequest;

public interface AuthService {
  void signUp(SignUpRequest signUpRequest);
  LoginResponse login(LoginRequest loginRequest);
  void changePassword(String email, ChangePasswordRequest changePasswordRequest);
}
