package com.management.library.service.request.management.dto;

import com.management.library.domain.management.ManagementRequest;
import com.management.library.domain.type.RequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManagementRequestServiceDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {
    private String title;
    private String content;

    @Builder
    private Request(String title, String content) {
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
      this.requestStatus = requestStatus;
      this.memberName = memberName;
    }

    public static Response of(ManagementRequest entity) {
      return Response.builder()
          .id(entity.getId())
          .title(entity.getTitle())
          .content(entity.getContent())
          .memberName(entity.getMember().getName())
          .requestStatus(entity.getRequestStatus())
          .build();
    }
  }
}
