package com.management.library.controller.member.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberNewBookRequestOverviewDto {

  @ApiModelProperty(example = "1")
  private Long id;
  @ApiModelProperty(example = "requestBookTitle")
  private String requestBookTitle;
  @ApiModelProperty(example = "requestContent")
  private String requestContent;
  @ApiModelProperty(example = "AWAIT")
  private RequestStatus requestStatus;

  @Builder
  private MemberNewBookRequestOverviewDto(Long id, String requestBookTitle, String requestContent,
      RequestStatus requestStatus) {
    this.id = id;
    this.requestBookTitle = requestBookTitle;
    this.requestContent = requestContent;
    this.requestStatus = requestStatus;
  }

  public static MemberNewBookRequestOverviewDto of(NewBookRequestServiceDto.Response response) {
    return MemberNewBookRequestOverviewDto.builder()
        .id(response.getId())
        .requestBookTitle(response.getRequestBookTitle())
        .requestContent(response.getRequestContent())
        .requestStatus(response.getRequestStatus())
        .build();
  }
}
