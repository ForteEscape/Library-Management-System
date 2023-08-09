package com.management.library.controller.admin.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AdminSignInDto {

  @NotBlank(message = "해당 필드는 비어있으면 안됩니다.")
  private String adminEmail;
  @NotBlank(message = "해당 필드는 비어있으면 안됩니다.")
  private String password;

  public AdminSignInDto(String adminEmail, String password) {
    this.adminEmail = adminEmail;
    this.password = password;
  }
}
