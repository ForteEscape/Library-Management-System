package com.management.library.controller.request.newbook.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NewBookRequestControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {

    @ApiModelProperty(example = "requestBookTitle")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String requestBookTitle;
    @ApiModelProperty(example = "requestBookContent")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
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

    @ApiModelProperty(example = "1")
    private Long id;
    @ApiModelProperty(example = "sehunkim")
    private String memberName;
    @ApiModelProperty(example = "requestBookTitle")
    private String requestBookTitle;
    @ApiModelProperty(example = "requestBookContent")
    private String requestContent;
    @ApiModelProperty(example = "AWAIT")
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
