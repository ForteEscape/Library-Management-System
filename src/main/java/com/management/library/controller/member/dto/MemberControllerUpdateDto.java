package com.management.library.controller.member.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class MemberControllerUpdateDto {

  @ApiModelProperty("park")
  @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
  private String name;
  @ApiModelProperty("서울특별시")
  @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
  private String legion;
  @ApiModelProperty("강변")
  @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
  private String city;
  @ApiModelProperty("강남로")
  @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
  private String street;

  @Builder
  private MemberControllerUpdateDto(String name, String legion, String city, String street) {
    this.name = name;
    this.legion = legion;
    this.city = city;
    this.street = street;
  }
}
