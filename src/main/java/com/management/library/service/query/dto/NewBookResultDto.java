package com.management.library.service.query.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class NewBookResultDto {

  private String adminName;
  private String resultPostTitle;
  private String resultPostContent;
  private RequestStatus resultStatus;

  @Builder
  private NewBookResultDto(String adminName, String resultPostTitle, String resultPostContent,
      RequestStatus resultStatus) {
    this.adminName = adminName;
    this.resultPostTitle = resultPostTitle;
    this.resultPostContent = resultPostContent;
    this.resultStatus = resultStatus;
  }

  public static NewBookResultDto of(NewBookResultCreateDto.Response response){
    return NewBookResultDto.builder()
        .adminName(response.getAdminName())
        .resultPostTitle(response.getResultPostTitle())
        .resultPostContent(response.getResultPostContent())
        .resultStatus(response.getResultStatus())
        .build();
  }
}
