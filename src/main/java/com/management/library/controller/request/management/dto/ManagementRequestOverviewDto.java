package com.management.library.controller.request.management.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagementRequestOverviewDto {

  @ApiModelProperty(example = "1")
  private Long id;
  @ApiModelProperty(example = "sehunkim")
  private String memberName;
  @ApiModelProperty(example = "managementRequestTitle")
  private String requestBookTitle;
  @ApiModelProperty(example = "AWAIT")
  private RequestStatus requestStatus;

  @Builder
  private ManagementRequestOverviewDto(Long id, String memberName, String requestBookTitle,
      RequestStatus requestStatus) {
    this.id = id;
    this.memberName = memberName;
    this.requestBookTitle = requestBookTitle;
    this.requestStatus = requestStatus;
  }

  public static ManagementRequestOverviewDto of(
      ManagementRequestServiceDto.Response response) {

    return ManagementRequestOverviewDto.builder()
        .id(response.getId())
        .memberName(response.getMemberName())
        .requestBookTitle(response.getTitle())
        .requestStatus(response.getRequestStatus())
        .build();
  }
}
