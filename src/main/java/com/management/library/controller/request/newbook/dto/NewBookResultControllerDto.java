package com.management.library.controller.request.newbook.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NewBookResultControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {

    private String resultTitle;
    private String resultContent;
    private RequestStatus resultStatus;

    @Builder
    private Request(String resultTitle, String resultContent, RequestStatus resultStatus) {
      this.resultTitle = resultTitle;
      this.resultContent = resultContent;
      this.resultStatus = resultStatus;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response {

    private Long id;
    private String newBookRequestTitle;
    private String adminName;
    private String resultPostTitle;
    private String resultPostContent;
    private RequestStatus resultStatus;

    @Builder
    private Response(Long id, String newBookRequestTitle, String adminName, String resultPostTitle,
        String resultPostContent, RequestStatus resultStatus) {
      this.id = id;
      this.newBookRequestTitle = newBookRequestTitle;
      this.adminName = adminName;
      this.resultPostTitle = resultPostTitle;
      this.resultPostContent = resultPostContent;
      this.resultStatus = resultStatus;
    }

    public static Response of(NewBookResultCreateDto.Response response){
      return Response.builder()
          .id(response.getId())
          .newBookRequestTitle(response.getNewBookRequestTitle())
          .adminName(response.getAdminName())
          .resultPostTitle(response.getResultPostTitle())
          .resultPostContent(response.getResultPostContent())
          .resultStatus(response.getResultStatus())
          .build();
    }
  }
}
