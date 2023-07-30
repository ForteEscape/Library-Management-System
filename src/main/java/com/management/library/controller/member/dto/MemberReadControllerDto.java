package com.management.library.controller.member.dto;

import com.management.library.service.member.dto.MemberReadServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberReadControllerDto {
  private String name;
  private String memberCode;
  private String birthdayCode;

  @Builder
  private MemberReadControllerDto(String name, String memberCode, String birthdayCode) {
    this.name = name;
    this.memberCode = memberCode;
    this.birthdayCode = birthdayCode;
  }

  public static MemberReadControllerDto of(MemberReadServiceDto member){
    return MemberReadControllerDto.builder()
        .name(member.getName())
        .memberCode(member.getMemberCode())
        .birthdayCode(member.getBirthdayCode())
        .build();
  }
}
