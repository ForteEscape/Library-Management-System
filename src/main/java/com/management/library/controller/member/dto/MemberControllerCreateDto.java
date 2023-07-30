package com.management.library.controller.member.dto;

import com.management.library.service.member.dto.MemberCreateServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberControllerCreateDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{
    private String name;
    private String birthdayCode;
    private String legion;
    private String city;
    private String street;

    @Builder
    public Request(String name, String birthdayCode, String legion, String city, String street) {
      this.name = name;
      this.birthdayCode = birthdayCode;
      this.legion = legion;
      this.city = city;
      this.street = street;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response{
    private String name;
    private String birthdayCode;
    private String legion;
    private String city;
    private String street;
    private String memberCode;
    private String password;

    @Builder
    private Response(String name, String birthdayCode, String legion, String city, String street,
        String memberCode, String password) {
      this.name = name;
      this.birthdayCode = birthdayCode;
      this.legion = legion;
      this.city = city;
      this.street = street;
      this.memberCode = memberCode;
      this.password = password;
    }

    public static Response of(MemberCreateServiceDto.Response response){
      return Response.builder()
          .name(response.getName())
          .birthdayCode(response.getBirthdayCode())
          .legion(response.getLegion())
          .city(response.getCity())
          .street(response.getStreet())
          .memberCode(response.getMemberCode())
          .password(response.getPassword())
          .build();
    }
  }
}
