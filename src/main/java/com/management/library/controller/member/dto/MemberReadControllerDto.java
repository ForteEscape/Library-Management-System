package com.management.library.controller.member.dto;

import com.management.library.service.member.dto.MemberServiceReadDto;
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
  private String legion;
  private String city;
  private String street;

  @Builder
  private MemberReadControllerDto(String name, String memberCode, String birthdayCode, String legion,
      String city, String street) {
    this.name = name;
    this.memberCode = memberCode;
    this.birthdayCode = birthdayCode;
    this.legion = legion;
    this.city = city;
    this.street = street;
  }

  public static MemberReadControllerDto of(MemberServiceReadDto member){
    return MemberReadControllerDto.builder()
        .name(member.getName())
        .memberCode(member.getMemberCode())
        .birthdayCode(member.getBirthdayCode())
        .legion(member.getLegion())
        .city(member.getCity())
        .street(member.getStreet())
        .build();
  }
}
