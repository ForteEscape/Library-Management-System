package com.management.library.service.query.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ManagementResultDto {

  private String adminName;
  private String resultPostTitle;
  private String resultPostContent;
  private RequestStatus resultStatus;

  @Builder
  private ManagementResultDto(String adminName, String resultPostTitle, String resultPostContent,
      RequestStatus resultStatus) {
    this.adminName = adminName;
    this.resultPostTitle = resultPostTitle;
    this.resultPostContent = resultPostContent;
    this.resultStatus = resultStatus;
  }

  public static ManagementResultDto of(ManagementResultCreateDto.Response response) {
    return ManagementResultDto.builder()
        .adminName(response.getAdminName())
        .resultPostTitle(response.getResultPostTitle())
        .resultPostContent(response.getResultPostContent())
        .resultStatus(response.getResultStatus())
        .build();
  }
}
