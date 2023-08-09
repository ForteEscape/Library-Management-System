package com.management.library.controller.member.dto;

import com.management.library.service.member.dto.MemberServiceReadDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberOverviewDto {

  @ApiModelProperty(example = "1")
  private Long id;
  @ApiModelProperty(example = "sehunkim")
  private String name;

  private MemberOverviewDto(Long id, String name) {
    this.id = id;
    this.name = name;
  }

  public static MemberOverviewDto of(MemberServiceReadDto data){
    return new MemberOverviewDto(data.getId(), data.getName());
  }
}
