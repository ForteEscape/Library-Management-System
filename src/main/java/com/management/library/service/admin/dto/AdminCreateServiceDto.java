package com.management.library.service.admin.dto;

import com.management.library.controller.admin.dto.AdminCreateControllerDto;
import com.management.library.domain.admin.Administrator;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class AdminCreateServiceDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{
    private String email;
    private String name;
    private String password;

    @Builder
    private Request(String email, String name, String password) {
      this.email = email;
      this.name = name;
      this.password = password;
    }

    public static Request of(AdminCreateControllerDto request){
      return null;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response{
    private Long id;
    private String name;
    private String email;

    @Builder
    private Response(Long id, String name, String email) {
      this.id = id;
      this.name = name;
      this.email = email;
    }

    public static Response of(Administrator admin){
      return Response.builder()
          .id(admin.getId())
          .email(admin.getEmail())
          .name(admin.getName())
          .build();
    }
  }
}
