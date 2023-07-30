package com.management.library.controller.admin;

import static com.management.library.controller.admin.dto.AdminCreateControllerDto.Request;
import static com.management.library.controller.admin.dto.AdminCreateControllerDto.Response;

import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminCreateServiceDto;
import com.management.library.service.rental.RentalService;
import lombok.RequiredArgsConstructor;
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
  private final RentalService rentalService;

  @PostMapping
  public Response createAdmin(@RequestBody Request request) {
    AdminCreateServiceDto.Response admin = adminService.createAdmin(
        AdminCreateServiceDto.Request.of(request));

    return Response.of(admin);
  }

}
