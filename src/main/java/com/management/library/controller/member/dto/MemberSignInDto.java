package com.management.library.controller.member.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberSignInDto {

  @ApiModelProperty(example = "100000001")
  @NotBlank(message = "해당 필드는 비어있으면 안됩니다.")
  private String memberCode;
  @ApiModelProperty(example = "980506!@#")
  @NotBlank(message = "해당 필드는 비어있으면 안됩니다.")
  private String password;

  public MemberSignInDto(String memberCode, String password) {
    this.memberCode = memberCode;
    this.password = password;
  }
}
