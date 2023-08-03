package com.management.library.service.query.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeDto {

  @NotBlank
  private String currentPassword;
  @NotBlank
  private String newPassword;

  @Builder
  private PasswordChangeDto(String currentPassword, String newPassword) {
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
  }
}
