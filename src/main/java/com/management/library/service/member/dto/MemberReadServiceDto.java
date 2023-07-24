package com.management.library.service.member.dto;

import com.management.library.domain.member.Member;
import com.management.library.domain.type.MemberRentalStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberReadServiceDto {

  private String name;
  private String memberCode;
  private MemberRentalStatus memberRentalStatus;

  @Builder
  private MemberReadServiceDto(String name, String memberCode,
      MemberRentalStatus memberRentalStatus) {
    this.name = name;
    this.memberCode = memberCode;
    this.memberRentalStatus = memberRentalStatus;
  }

  public static MemberReadServiceDto of(Member member){
    return MemberReadServiceDto.builder()
        .name(member.getName())
        .memberCode(member.getMemberCode())
        .memberRentalStatus(member.getMemberRentalStatus())
        .build();
  }
}
