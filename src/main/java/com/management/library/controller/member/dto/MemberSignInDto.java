package com.management.library.controller.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberSignInDto {

  @NotBlank(message = "해당 필드는 비어있으면 안됩니다.")
  private String memberCode;
  @NotBlank(message = "해당 필드는 비어있으면 안됩니다.")
  private String password;

  public MemberSignInDto(String memberCode, String password) {
    this.memberCode = memberCode;
    this.password = password;
  }
}
