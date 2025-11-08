package com.atlasculinary.services.impl;

import com.atlasculinary.dtos.ChangePasswordRequest;
import com.atlasculinary.dtos.LoginRequest;
import com.atlasculinary.dtos.LoginResponse;
import com.atlasculinary.dtos.SignUpRequest;
import com.atlasculinary.entities.*;
import com.atlasculinary.enums.AccountStatus;
import com.atlasculinary.enums.RoleLevel;
import com.atlasculinary.repositories.*;
import com.atlasculinary.services.AuthService;
import com.atlasculinary.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final Logger LOGGER = Logger.getLogger(AuthServiceImpl.class.getName());
  private final AccountRepository accountRepository;
  private final RoleRepository roleRepository;
  private final AccountRoleMapRepository accountRoleMapRepository;
  private final UserRepository userRepository;
  private final AdminRepository adminRepository;
  private final VendorRepository vendorRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;
  private final AuthenticationManager authenticationManager;

  @Override
  @Transactional
  public void signUp(SignUpRequest signUpRequest) {
    if (accountRepository.existsByEmail(signUpRequest.getEmail())) {
      throw new RuntimeException("Email đã tồn tại trong hệ thống");
    }

    Account account = new Account();
    account.setEmail(signUpRequest.getEmail());
    account.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
    account.setStatus(AccountStatus.ACTIVE);

    List<Role> roleList = roleRepository.findAll();
    for (var role: roleList) {
        LOGGER.severe(role.getRoleName() + " " +  role.getDescription());
    }
    Role role = roleRepository.findByRoleName(signUpRequest.getRole().name())
            .orElseThrow(() -> new RuntimeException("Role không tồn tại"));

    Account savedAccount = accountRepository.save(account);

    AccountRoleMap accountRoleMap = new AccountRoleMap();
    accountRoleMap.setAccount(savedAccount);
    accountRoleMap.setRole(role);
    accountRoleMap.setAccountId(savedAccount.getAccountId());
    accountRoleMap.setRoleId(role.getRoleId());
    accountRoleMapRepository.save(accountRoleMap);

    // Tạo  tương ứng với role
    createForAccount(savedAccount, signUpRequest.getRole());
  }

  private void createForAccount(Account account, com.atlasculinary.enums.AccountRole role) {
    switch (role) {
      case USER:
        UserProfile user = new UserProfile();
        user.setAccount(account);
        userRepository.save(user);
        break;

      case ADMIN:
        AdminProfile admin = new AdminProfile();
        admin.setAccount(account);
        admin.setRoleLevel(RoleLevel.MODERATOR); // Default role level
        adminRepository.save(admin);
        break;

      case VENDOR:
        VendorProfile vendor = new VendorProfile();
        vendor.setAccount(account);
        vendorRepository.save(vendor);
        break;

      default:
        throw new RuntimeException("Role không được hỗ trợ");
    }
  }

  @Override
  public LoginResponse login(LoginRequest loginRequest) {
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(
              loginRequest.getEmail(),
              loginRequest.getPassword()
          )
      );

      Account account = accountRepository.findByEmail(loginRequest.getEmail())
          .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

      if (account.getStatus() == AccountStatus.BLOCKED) {
        throw new RuntimeException("Tài khoản đã bị khóa");
      }
      if (account.getStatus() == AccountStatus.DELETED) {
        throw new RuntimeException("Tài khoản đã bị xóa");
      }



      var roleMapList = accountRoleMapRepository.findByAccountIdWithRole(account.getAccountId());

      var roles = roleMapList.stream()
        .map(roleMap -> {
          return roleMap.getRole().getRoleName();
        })
        .collect(Collectors.toList());
      LOGGER.severe("Roles" + roles);
      String token = jwtUtil.generateToken(account.getEmail(), roles);

      return LoginResponse.builder()
          .token(token)
          .email(account.getEmail())
          .fullName(account.getFullName())
          .avatarUrl(account.getAvatarUrl())
          .roles(roles)
          .build();

    } catch (AuthenticationException e) {
      throw new RuntimeException("Email hoặc mật khẩu không chính xác");
    }
  }

  @Override
  @Transactional
  public void changePassword(String email, ChangePasswordRequest changePasswordRequest) {
    if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
      throw new RuntimeException("Mật khẩu mới và xác nhận mật khẩu không khớp");
    }

    Account account = accountRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

    if (account.getStatus() == AccountStatus.BLOCKED) {
      throw new RuntimeException("Tài khoản đã bị khóa");
    }
    if (account.getStatus() == AccountStatus.DELETED) {
      throw new RuntimeException("Tài khoản đã bị xóa");
    }

    if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), account.getPassword())) {
      throw new RuntimeException("Mật khẩu hiện tại không đúng");
    }

    if (passwordEncoder.matches(changePasswordRequest.getNewPassword(), account.getPassword())) {
      throw new RuntimeException("Mật khẩu mới phải khác mật khẩu hiện tại");
    }

    account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
    accountRepository.save(account);

    LOGGER.info("Đổi mật khẩu thành công cho tài khoản: " + email);
  }
}
