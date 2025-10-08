package com.atlasculinary.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    
  private String token;
  
  @Builder.Default
  private String tokenType = "Bearer";
  
  private String email;
  private String fullName;
  private String avatarUrl;
  
  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> roles;
}
