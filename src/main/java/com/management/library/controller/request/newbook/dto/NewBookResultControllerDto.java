package com.management.library.controller.request.newbook.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class NewBookResultControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class NewBookResultRequest {

    @ApiModelProperty(example = "resultTitle")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String resultTitle;
    @ApiModelProperty(example = "resultContent")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String resultContent;
    @ApiModelProperty(example = "ACCEPTED")
    private RequestStatus resultStatus;

    @Builder
    private NewBookResultRequest(String resultTitle, String resultContent,
        RequestStatus resultStatus) {
      this.resultTitle = resultTitle;
      this.resultContent = resultContent;
      this.resultStatus = resultStatus;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class NewBookResultResponse {

    @ApiModelProperty(example = "1")
    private Long id;
    @ApiModelProperty(example = "newBookRequestTitle")
    private String newBookRequestTitle;
    @ApiModelProperty(example = "admin1")
    private String adminName;
    @ApiModelProperty(example = "resultPostTitle")
    private String resultPostTitle;
    @ApiModelProperty(example = "resultPostContent")
    private String resultPostContent;
    @ApiModelProperty(example = "ACCEPTED")
    private RequestStatus resultStatus;

    @Builder
    private NewBookResultResponse(Long id, String newBookRequestTitle, String adminName,
        String resultPostTitle, String resultPostContent, RequestStatus resultStatus) {
      this.id = id;
      this.newBookRequestTitle = newBookRequestTitle;
      this.adminName = adminName;
      this.resultPostTitle = resultPostTitle;
      this.resultPostContent = resultPostContent;
      this.resultStatus = resultStatus;
    }

    public static NewBookResultResponse of(NewBookResultCreateDto.Response response) {
      return NewBookResultResponse.builder()
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
