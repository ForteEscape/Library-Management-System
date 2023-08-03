package com.management.library.controller.member.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class MemberControllerUpdateDto {

  private String name;
  private String legion;
  private String city;
  private String street;

  @Builder
  private MemberControllerUpdateDto(String name, String legion, String city, String street) {
    this.name = name;
    this.legion = legion;
    this.city = city;
    this.street = street;
  }
}
