package com.management.library.service.member.dto;

import com.management.library.controller.member.dto.MemberControllerUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberServiceUpdateDto {

  private String name;
  private String legion;
  private String city;
  private String street;

  @Builder
  private MemberServiceUpdateDto(String name, String legion, String city, String street) {
    this.name = name;
    this.legion = legion;
    this.city = city;
    this.street = street;
  }

  public static MemberServiceUpdateDto of(MemberControllerUpdateDto request) {
    return MemberServiceUpdateDto.builder()
        .name(request.getName())
        .legion(request.getLegion())
        .city(request.getCity())
        .street(request.getCity())
        .build();
  }
}
