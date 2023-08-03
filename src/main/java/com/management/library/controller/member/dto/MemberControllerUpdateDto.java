package com.management.library.controller.member.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
public class MemberControllerUpdateDto {

  @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
  private String name;
  @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
  private String legion;
  @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
  private String city;
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
