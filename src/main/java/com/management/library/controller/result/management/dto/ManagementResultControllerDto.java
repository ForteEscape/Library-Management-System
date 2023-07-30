package com.management.library.controller.result.management.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManagementResultControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{
    private String resultPostTitle;
    private String resultPostContent;
    private RequestStatus resultStatus;

    public Request(String resultPostTitle, String resultPostContent, RequestStatus resultStatus) {
      this.resultPostTitle = resultPostTitle;
      this.resultPostContent = resultPostContent;
      this.resultStatus = resultStatus;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response{
    private Long id;
    private String managementRequestTitle;
    private String adminName;
    private String resultPostTitle;
    private String resultPostContent;
    private RequestStatus resultStatus;

    @Builder
    private Response(Long id, String managementRequestTitle, String adminName,
        String resultPostTitle, String resultPostContent, RequestStatus resultStatus) {
      this.id = id;
      this.managementRequestTitle = managementRequestTitle;
      this.adminName = adminName;
      this.resultPostTitle = resultPostTitle;
      this.resultPostContent = resultPostContent;
      this.resultStatus = resultStatus;
    }

    public static Response of(ManagementResultCreateDto.Response response){
      return Response.builder()
          .id(response.getId())
          .adminName(response.getAdminName())
          .managementRequestTitle(response.getManagementRequestTitle())
          .resultPostTitle(response.getResultPostTitle())
          .resultPostContent(response.getResultPostContent())
          .resultStatus(response.getResultStatus())
          .build();
    }
  }

}