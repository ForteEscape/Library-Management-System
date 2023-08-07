package com.management.library.service.member.dto;

import com.management.library.domain.type.Authority;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignInResultDto {

  private String memberCode;
  private Authority authority;

  public MemberSignInResultDto(String memberCode, Authority authority) {
    this.memberCode = memberCode;
    this.authority = authority;
  }
}
