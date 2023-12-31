package com.management.library.controller.admin.dto;

import com.management.library.service.admin.dto.AdminServiceCreateDto;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AdminControllerCreateDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class AdminCreateRequest {

    @ApiModelProperty(example = "admin1@test.com")
    @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
    private String email;
    @ApiModelProperty(example = "admin1")
    @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
    private String name;
    @ApiModelProperty(example = "1234")
    @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
    private String password;

    public AdminCreateRequest(String email, String name, String password) {
      this.email = email;
      this.name = name;
      this.password = password;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class AdminCreateResponse {

    @ApiModelProperty(example = "1")
    private Long id;
    @ApiModelProperty(example = "admin1")
    private String name;
    @ApiModelProperty(example = "admin1@test.com")
    private String email;

    @Builder
    private AdminCreateResponse(Long id, String name, String email) {
      this.id = id;
      this.name = name;
      this.email = email;
    }

    public static AdminCreateResponse of(AdminServiceCreateDto.Response response) {
      return AdminCreateResponse.builder()
          .id(response.getId())
          .name(response.getName())
          .email(response.getEmail())
          .build();
    }
  }
}
