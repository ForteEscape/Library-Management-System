package com.management.library.service.member.dto;

import com.management.library.controller.member.dto.MemberControllerCreateDto;
import com.management.library.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberCreateServiceDto {

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
    private Request(String name, String birthdayCode, String legion, String city, String street) {
      this.name = name;
      this.birthdayCode = birthdayCode;
      this.legion = legion;
      this.city = city;
      this.street = street;
    }

    public static Request of(MemberControllerCreateDto.Request request){
      return Request.builder()
          .name(request.getName())
          .birthdayCode(request.getBirthdayCode())
          .legion(request.getLegion())
          .city(request.getCity())
          .street(request.getStreet())
          .build();
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
        String memberCode) {
      this.name = name;
      this.birthdayCode = birthdayCode;
      this.legion = legion;
      this.city = city;
      this.street = street;
      this.memberCode = memberCode;
    }

    public static Response of(Member member){
      return Response.builder()
          .name(member.getName())
          .birthdayCode(member.getBirthdayCode())
          .legion(member.getAddress().getLegion())
          .city(member.getAddress().getCity())
          .street(member.getAddress().getStreet())
          .memberCode(member.getMemberCode())
          .build();
    }
  }

}
