package com.management.library.controller.request.management.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManagementResultControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ManagementResultRequest {

    @ApiModelProperty(example = "resultPostTitle")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String resultPostTitle;
    @ApiModelProperty(example = "resultPostContent")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String resultPostContent;
    @ApiModelProperty(example = "ACCEPTED")
    private RequestStatus resultStatus;

    public ManagementResultRequest(String resultPostTitle, String resultPostContent,
        RequestStatus resultStatus) {
      this.resultPostTitle = resultPostTitle;
      this.resultPostContent = resultPostContent;
      this.resultStatus = resultStatus;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ManagementResultResponse {

    @ApiModelProperty(example = "1")
    private Long id;
    @ApiModelProperty(example = "managementRequestTitle")
    private String managementRequestTitle;
    @ApiModelProperty(example = "admin1")
    private String adminName;
    @ApiModelProperty(example = "resultPostTitle")
    private String resultPostTitle;
    @ApiModelProperty(example = "resultPostContent")
    private String resultPostContent;
    @ApiModelProperty(example = "ACCEPTED")
    private RequestStatus resultStatus;

    @Builder
    private ManagementResultResponse(Long id, String managementRequestTitle, String adminName,
        String resultPostTitle, String resultPostContent, RequestStatus resultStatus) {
      this.id = id;
      this.managementRequestTitle = managementRequestTitle;
      this.adminName = adminName;
      this.resultPostTitle = resultPostTitle;
      this.resultPostContent = resultPostContent;
      this.resultStatus = resultStatus;
    }

    public static ManagementResultResponse of(ManagementResultCreateDto.Response response) {
      return ManagementResultResponse.builder()
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
