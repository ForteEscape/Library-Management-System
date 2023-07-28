package com.management.library.service.result.newbook.dto;

import com.management.library.domain.newbook.NewBookRequestResult;
import com.management.library.domain.type.RequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NewBookResultCreateDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{
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
  public static class Response{
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

    public static Response of(NewBookRequestResult result){
      return Response.builder()
          .id(result.getId())
          .newBookRequestTitle(result.getNewBookRequest().getRequestBookTitle())
          .adminName(result.getAdministrator().getName())
          .resultPostTitle(result.getResultPostTitle())
          .resultPostContent(result.getResultPostContent())
          .resultStatus(result.getResult())
          .build();
    }
  }

}
