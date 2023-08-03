package com.management.library.controller.request.newbook.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NewBookRequestOverviewDto {

  private Long id;
  private String memberName;
  private String requestBookTitle;
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
