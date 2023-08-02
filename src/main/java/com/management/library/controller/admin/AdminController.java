package com.management.library.controller.admin;

import static com.management.library.controller.admin.dto.AdminCreateControllerDto.Request;
import static com.management.library.controller.admin.dto.AdminCreateControllerDto.Response;

import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminCreateServiceDto;
import com.management.library.service.result.management.ManagementResultService;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import com.management.library.service.result.newbook.NewBookResultService;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 기능 사용 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {

  private final AdminService adminService;
  private final ManagementResultService managementResultService;
  private final NewBookResultService newBookResultService;

  @PostMapping
  public Response createAdmin(@RequestBody Request request) {
    AdminCreateServiceDto.Response admin = adminService.createAdmin(
        AdminCreateServiceDto.Request.of(request));

    return Response.of(admin);
  }

  @GetMapping("/my-management-results")
  public Page<ManagementResultCreateDto.Response> getManagementResult(Principal principal,
      Pageable pageable) {
    return managementResultService.getResultByAdminEmail(principal.getName(), pageable);
  }

  @GetMapping("/my-new-book-results")
  public Page<NewBookResultCreateDto.Response> getNewBookResult(Principal principal,
      Pageable pageable) {
    return newBookResultService.getResultByAdminEmail(principal.getName(), pageable);
  }

}
