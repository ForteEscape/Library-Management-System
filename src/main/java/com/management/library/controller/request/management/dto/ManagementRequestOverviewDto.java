package com.management.library.controller.request.management.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ManagementRequestOverviewDto {

  private Long id;
  private String memberName;
  private String requestBookTitle;
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
