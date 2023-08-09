package com.management.library.service.query.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeDto {

  @ApiModelProperty(example = "980506!@#")
  @NotBlank
  private String currentPassword;
  @ApiModelProperty(example = "1234")
  @NotBlank
  private String newPassword;

  @Builder
  private PasswordChangeDto(String currentPassword, String newPassword) {
    this.currentPassword = currentPassword;
    this.newPassword = newPassword;
  }
}
