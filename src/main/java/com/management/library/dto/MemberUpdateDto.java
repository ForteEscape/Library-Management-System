package com.management.library.dto;

import com.management.library.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberUpdateDto {


  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    private String name;
    private String legion;
    private String city;
    private String street;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    private String name;
    private String legion;
    private String city;
    private String street;

    public static Response fromEntity(Member member) {
      return Response.builder()
          .name(member.getName())
          .legion(member.getAddress().getLegion())
          .city(member.getAddress().getCity())
          .street(member.getAddress().getStreet())
          .build();
    }
  }
}
