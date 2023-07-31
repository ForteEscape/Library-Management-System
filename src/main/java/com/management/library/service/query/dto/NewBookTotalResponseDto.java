package com.management.library.service.query.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewBookTotalResponseDto {

  private String requestBookTitle;
  private String requestContent;
  private RequestStatus requestStatus;
  private NewBookResultDto resultDto;

  @Builder
  private NewBookTotalResponseDto(String requestBookTitle, String requestContent,
      RequestStatus requestStatus, NewBookResultDto resultDto) {
    this.requestBookTitle = requestBookTitle;
    this.requestContent = requestContent;
    this.requestStatus = requestStatus;
    this.resultDto = resultDto;
  }

  public static NewBookTotalResponseDto of(NewBookRequestServiceDto.Response request, NewBookResultDto result){
    return NewBookTotalResponseDto.builder()
        .requestBookTitle(request.getRequestBookTitle())
        .requestContent(request.getRequestContent())
        .requestStatus(request.getRequestStatus())
        .resultDto(result)
        .build();
  }
}
