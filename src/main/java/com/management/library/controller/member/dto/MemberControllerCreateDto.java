package com.management.library.controller.member.dto;

import com.management.library.service.member.dto.MemberServiceCreateDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class MemberControllerCreateDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String name;
    @Size(max = 6, min = 6, message = "생년월일은 6자리로 제한됩니다.")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String birthdayCode;
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String legion;
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String city;
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
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

    public static Response of(MemberServiceCreateDto.Response response){
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
