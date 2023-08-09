package com.management.library.controller.member.dto;

import com.management.library.service.member.dto.MemberServiceCreateDto;
import io.swagger.annotations.ApiModelProperty;
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
  public static class MemberCreateRequest {

    @ApiModelProperty(example = "sehunkim")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String name;
    @ApiModelProperty(example = "980506")
    @Size(max = 6, min = 6, message = "생년월일은 6자리로 제한됩니다.")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String birthdayCode;
    @ApiModelProperty(example = "경상남도")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String legion;
    @ApiModelProperty(example = "김해시")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String city;
    @ApiModelProperty(example = "삼계로")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String street;

    @Builder
    public MemberCreateRequest(String name, String birthdayCode, String legion, String city,
        String street) {
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
  public static class MemberCreateResponse {

    @ApiModelProperty(example = "sehunkim")
    private String name;
    @ApiModelProperty(example = "980506")
    private String birthdayCode;
    @ApiModelProperty(example = "경상남도")
    private String legion;
    @ApiModelProperty(example = "김해시")
    private String city;
    @ApiModelProperty(example = "삼계로")
    private String street;
    @ApiModelProperty(example = "100000001")
    private String memberCode;
    @ApiModelProperty(example = "980506!@#")
    private String password;

    @Builder
    private MemberCreateResponse(String name, String birthdayCode, String legion, String city,
        String street, String memberCode, String password) {
      this.name = name;
      this.birthdayCode = birthdayCode;
      this.legion = legion;
      this.city = city;
      this.street = street;
      this.memberCode = memberCode;
      this.password = password;
    }

    public static MemberCreateResponse of(MemberServiceCreateDto.Response response) {
      return MemberCreateResponse.builder()
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
