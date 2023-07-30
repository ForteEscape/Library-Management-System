package com.management.library.controller.request.management;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManagementRequestControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {
    private String title;
    private String content;

    public Request(String title, String content) {
      this.title = title;
      this.content = content;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response {
    private Long id;
    private String title;
    private String content;
    private String memberName;
    private RequestStatus requestStatus;

    @Builder
    private Response(Long id, String title, String content, String memberName,
        RequestStatus requestStatus) {
      this.id = id;
      this.title = title;
      this.content = content;
      this.memberName = memberName;
      this.requestStatus = requestStatus;
    }

    public static Response of(ManagementRequestServiceDto.Response response){
      return Response.builder()
          .id(response.getId())
          .title(response.getTitle())
          .content(response.getContent())
          .memberName(response.getMemberName())
          .requestStatus(response.getRequestStatus())
          .build();
    }
  }

}
