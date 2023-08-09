package com.management.library.service.admin.dto;

import com.management.library.domain.type.Authority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminSignInResultDto {

  private String adminEmail;
  private Authority authority;

  public AdminSignInResultDto(String adminEmail, Authority authority) {
    this.adminEmail = adminEmail;
    this.authority = authority;
  }
}
