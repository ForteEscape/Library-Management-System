package com.management.library.controller.request.management.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManagementResultControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String resultPostTitle;
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String resultPostContent;
    @NotEmpty(message = "해당 부분은 비어있으면 안됩니다.")
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
