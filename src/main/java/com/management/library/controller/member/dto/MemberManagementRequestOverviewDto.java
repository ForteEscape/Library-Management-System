package com.management.library.controller.member.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberManagementRequestOverviewDto {

  @ApiModelProperty(example = "1")
  private Long id;
  @ApiModelProperty(example = "managementRequestTitle")
  private String title;
  @ApiModelProperty(example = "managementRequestContent")
  private String content;
  @ApiModelProperty(example = "AWAIT")
  private RequestStatus requestStatus;

  @Builder
  private MemberManagementRequestOverviewDto(Long id, String title, String content,
      RequestStatus requestStatus) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.requestStatus = requestStatus;
  }

  public static MemberManagementRequestOverviewDto of(
      ManagementRequestServiceDto.Response response) {
    return MemberManagementRequestOverviewDto.builder()
        .id(response.getId())
        .title(response.getTitle())
        .content(response.getContent())
        .requestStatus(response.getRequestStatus())
        .build();
  }
}
