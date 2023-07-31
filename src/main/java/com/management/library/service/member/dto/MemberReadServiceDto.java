package com.management.library.service.member.dto;

import com.management.library.domain.member.Member;
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
  private String birthdayCode;
  private String legion;
  private String city;
  private String street;

  @Builder
  private MemberReadServiceDto(String name, String memberCode, String birthdayCode, String legion,
      String city, String street) {
    this.name = name;
    this.memberCode = memberCode;
    this.birthdayCode = birthdayCode;
    this.legion = legion;
    this.city = city;
    this.street = street;
  }

  public static MemberReadServiceDto of(Member member){
    return MemberReadServiceDto.builder()
        .name(member.getName())
        .memberCode(member.getMemberCode())
        .birthdayCode(member.getBirthdayCode())
        .legion(member.getAddress().getLegion())
        .city(member.getAddress().getCity())
        .street(member.getAddress().getStreet())
        .build();
  }
}
