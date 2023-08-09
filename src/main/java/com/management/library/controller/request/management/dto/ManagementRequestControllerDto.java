package com.management.library.controller.request.management.dto;

import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ManagementRequestControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ManagementCreateRequest {

    @ApiModelProperty(example = "managementRequestTitle")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String title;
    @ApiModelProperty(example = "managementRequestContent")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String content;

    public ManagementCreateRequest(String title, String content) {
      this.title = title;
      this.content = content;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ManagementCreateResponse {

    @ApiModelProperty(example = "1")
    private Long id;
    @ApiModelProperty(example = "managementRequestTitle")
    private String title;
    @ApiModelProperty(example = "managementRequestContent")
    private String content;
    @ApiModelProperty(example = "sehunkim")
    private String memberName;
    @ApiModelProperty(example = "AWAIT")
    private RequestStatus requestStatus;

    @Builder
    private ManagementCreateResponse(Long id, String title, String content, String memberName,
        RequestStatus requestStatus) {
      this.id = id;
      this.title = title;
      this.content = content;
      this.memberName = memberName;
      this.requestStatus = requestStatus;
    }

    public static ManagementCreateResponse of(ManagementRequestServiceDto.Response response) {
      return ManagementCreateResponse.builder()
          .id(response.getId())
          .title(response.getTitle())
          .content(response.getContent())
          .memberName(response.getMemberName())
          .requestStatus(response.getRequestStatus())
          .build();
    }
  }

}
