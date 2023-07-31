package com.management.library.service.member.dto;

import com.management.library.controller.member.dto.MemberUpdateControllerDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberUpdateServiceDto {

  private String name;
  private String legion;
  private String city;
  private String street;

  @Builder
  private MemberUpdateServiceDto(String name, String legion, String city, String street) {
    this.name = name;
    this.legion = legion;
    this.city = city;
    this.street = street;
  }

  public static MemberUpdateServiceDto of(MemberUpdateControllerDto request) {
    return MemberUpdateServiceDto.builder()
        .name(request.getName())
        .legion(request.getLegion())
        .city(request.getCity())
        .street(request.getCity())
        .build();
  }
}
