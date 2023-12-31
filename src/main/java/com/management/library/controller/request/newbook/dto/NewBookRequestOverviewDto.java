package com.management.library.controller.request.newbook.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewBookRequestOverviewDto {

  @ApiModelProperty(example = "1")
  private Long id;
  @ApiModelProperty(example = "sehunkim")
  private String memberName;
  @ApiModelProperty(example = "requestBookTitle")
  private String requestBookTitle;
  @ApiModelProperty(example = "AWAIT")
  private RequestStatus requestStatus;

  @Builder
  private NewBookRequestOverviewDto(Long id, String memberName, String requestBookTitle,
      RequestStatus requestStatus) {
    this.id = id;
    this.memberName = memberName;
    this.requestBookTitle = requestBookTitle;
    this.requestStatus = requestStatus;
  }

  public static NewBookRequestOverviewDto of(NewBookRequestServiceDto.Response response){
    return NewBookRequestOverviewDto.builder()
        .id(response.getId())
        .memberName(response.getMemberName())
        .requestBookTitle(response.getRequestBookTitle())
        .requestStatus(response.getRequestStatus())
        .build();
  }
}
