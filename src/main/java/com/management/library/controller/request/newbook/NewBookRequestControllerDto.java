package com.management.library.controller.request.newbook;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NewBookRequestControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {

    private String requestBookTitle;
    private String requestContent;

    @Builder
    private Request(String requestBookTitle, String requestContent) {
      this.requestBookTitle = requestBookTitle;
      this.requestContent = requestContent;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response {

    private Long id;
    private String memberName;
    private String requestBookTitle;
    private String requestContent;
    private RequestStatus requestStatus;

    @Builder
    private Response(Long id, String memberName, String requestBookTitle, String requestContent,
        RequestStatus requestStatus) {
      this.id = id;
      this.memberName = memberName;
      this.requestBookTitle = requestBookTitle;
      this.requestContent = requestContent;
      this.requestStatus = requestStatus;
    }

    public static Response of(NewBookRequestServiceDto.Response response) {
      return Response.builder()
          .id(response.getId())
          .memberName(response.getMemberName())
          .requestBookTitle(response.getRequestBookTitle())
          .requestContent(response.getRequestContent())
          .requestStatus(response.getRequestStatus())
          .build();
    }
  }
}