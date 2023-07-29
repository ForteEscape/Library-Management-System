package com.management.library.service.request.newbook.dto;

import com.management.library.domain.newbook.NewBookRequest;
import com.management.library.domain.type.RequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NewBookRequestServiceDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{
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
  public static class Response{
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

    public static Response of(NewBookRequest entity){
      return Response.builder()
          .id(entity.getId())
          .memberName(entity.getMember().getName())
          .requestBookTitle(entity.getRequestBookTitle())
          .requestContent(entity.getRequestContent())
          .requestStatus(entity.getRequestStatus())
          .build();
    }
  }
}
