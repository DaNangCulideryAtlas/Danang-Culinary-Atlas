package com.atlasculinary.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder);
    return authProvider;
  }

  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }
    // Các URI hoàn toàn công khai
    private static final String[] PUBLIC_BASE_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/webjars/**",
            "/api/v1/auth/**"
    };

    // URI cho phép truy cập GET công khai (tài nguyên chính và tài nguyên con để đọc)
    private static final String[] PUBLIC_GET_URLS = {
            // Tài nguyên chính: Nhà hàng, Món ăn, Review (cho phép /api/v1/restaurants, /api/v1/restaurants/{id}...)
            "/api/v1/restaurants/**",
            "/api/v1/dishes/**",
            "/api/v1/reviews/**",
            "/api/v1/restaurants/*/dishes",
            "/api/v1/restaurants/*/reviews",
            "/api/v1/dishes/*/reviews"
    };

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
              // 1. PUBLIC BASE URLS (Luôn cho phép)
              .requestMatchers(PUBLIC_BASE_URLS).permitAll()

              // 2. PUBLIC GET ACCESS (Đọc dữ liệu công khai)
              .requestMatchers(HttpMethod.GET, PUBLIC_GET_URLS).permitAll()

              // 3. ADMIN ENDPOINTS
              .requestMatchers("/api/v1/*/admin/**").hasAuthority("ADMIN")
              .requestMatchers("/api/v1/admin/**").hasAuthority("ADMIN")

              // 4. RESTAURANT & DISH ENDPOINTS
              .requestMatchers(HttpMethod.POST, "/api/v1/restaurants/**", "/api/v1/dishes/**").hasAnyAuthority("VENDOR", "ADMIN")
              .requestMatchers(HttpMethod.PUT, "/api/v1/restaurants/**", "/api/v1/dishes/**").hasAnyAuthority("VENDOR", "ADMIN")
              .requestMatchers(HttpMethod.PATCH, "/api/v1/restaurants/**", "/api/v1/dishes/**").hasAnyAuthority("VENDOR", "ADMIN")
              .requestMatchers(HttpMethod.DELETE, "/api/v1/restaurants/**", "/api/v1/dishes/**").hasAnyAuthority("VENDOR", "ADMIN")

              // 5. REVIEW ENDPOINTS
              .requestMatchers(HttpMethod.POST, "/api/v1/reviews/**").authenticated()
              .requestMatchers(HttpMethod.PUT, "/api/v1/reviews/**").authenticated()
              .requestMatchers(HttpMethod.PATCH, "/api/v1/reviews/**").authenticated()
              .requestMatchers(HttpMethod.DELETE, "/api/v1/reviews/**").authenticated()

              // 6. NOTIFICATION ENDPOINTS
              .requestMatchers("/api/v1/notifications/**").authenticated()

              // 7. PROFILE PATHS
              .requestMatchers("/api/v1/profile/admin/**").hasAuthority("ADMIN")
              .requestMatchers("/api/v1/profile/user/**").hasAnyAuthority("USER", "ADMIN")
              .requestMatchers("/api/v1/profile/vendor/**").hasAnyAuthority("VENDOR", "ADMIN")

              // 8. CATCH-ALL: Tất cả các request còn lại phải được xác thực
              .anyRequest().authenticated()
      )
        .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                    {"status":"error","message":"Unauthorized"}
                """);
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("""
                    {"status":"error","message":"Access Denied"}
                """);
                })
        )
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }


}
