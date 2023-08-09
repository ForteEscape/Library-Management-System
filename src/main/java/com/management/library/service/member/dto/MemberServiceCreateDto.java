package com.management.library.service.member.dto;

import com.management.library.controller.member.dto.MemberControllerCreateDto.MemberCreateRequest;
import com.management.library.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberServiceCreateDto {

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

    public static Request of(MemberCreateRequest memberCreateRequest){
      return Request.builder()
          .name(memberCreateRequest.getName())
          .birthdayCode(memberCreateRequest.getBirthdayCode())
          .legion(memberCreateRequest.getLegion())
          .city(memberCreateRequest.getCity())
          .street(memberCreateRequest.getStreet())
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
