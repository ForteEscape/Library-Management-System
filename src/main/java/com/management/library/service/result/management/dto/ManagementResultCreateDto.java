package com.management.library.service.result.management.dto;

import com.management.library.domain.management.ManagementRequestResult;
import com.management.library.domain.type.RequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManagementResultCreateDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {

    private String resultPostTitle;
    private String resultPostContent;
    private RequestStatus resultStatus;

    @Builder
    private Request(String resultPostTitle, String resultPostContent, RequestStatus resultStatus) {
      this.resultPostTitle = resultPostTitle;
      this.resultPostContent = resultPostContent;
      this.resultStatus = resultStatus;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response {

    private Long id;
    private String managementRequestTitle;
    private String adminName;
    private String resultPostTitle;
    private String resultPostContent;
    private RequestStatus resultStatus;

    @Builder
    private Response(Long id, String managementRequestTitle, String adminName,
        String resultPostTitle, String resultPostContent, RequestStatus requestStatus) {
      this.id = id;
      this.managementRequestTitle = managementRequestTitle;
      this.adminName = adminName;
      this.resultPostTitle = resultPostTitle;
      this.resultPostContent = resultPostContent;
      this.resultStatus = requestStatus;
    }

    public static Response of(ManagementRequestResult result){
      return Response.builder()
          .id(result.getId())
          .managementRequestTitle(result.getManagementRequest().getTitle())
          .adminName(result.getAdministrator().getName())
          .resultPostTitle(result.getResultPostTitle())
          .resultPostContent(result.getResultPostContent())
          .requestStatus(result.getResult())
          .build();
    }
  }
}
