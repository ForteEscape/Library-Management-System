package com.management.library.service.query.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManagementTotalResponseDto {

  private String requestTitle;
  private String requestContent;
  private RequestStatus requestStatus;
  private ManagementResultDto resultDto;

  @Builder
  private ManagementTotalResponseDto(String requestTitle, String requestContent,
      RequestStatus requestStatus, ManagementResultDto resultDto) {
    this.requestTitle = requestTitle;
    this.requestContent = requestContent;
    this.requestStatus = requestStatus;
    this.resultDto = resultDto;
  }

  public static ManagementTotalResponseDto of(ManagementRequestServiceDto.Response request,
      ManagementResultDto result) {
    return ManagementTotalResponseDto.builder()
        .requestTitle(request.getTitle())
        .requestContent(request.getContent())
        .requestStatus(request.getRequestStatus())
        .resultDto(result)
        .build();
  }
}
