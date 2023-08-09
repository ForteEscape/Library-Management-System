package com.management.library;

import com.management.library.controller.admin.dto.AdminControllerCreateDto.AdminCreateRequest;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminServiceCreateDto;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
public class InitData {

  private final AdminService adminService;
  private final AdministratorRepository administratorRepository;

  // 현재 구조상 한 명의 administrator 는 필수적으로 존재해야 한다.
  @PostConstruct
  public void initAdministrator() {
    AdminCreateRequest adminCreateRequest = new AdminCreateRequest("admin1@test.com", "admin1", "1234");

    boolean present = administratorRepository.findByEmail("admin1@test.com").isPresent();

    if (!present){
      adminService.createAdmin(AdminServiceCreateDto.Request.of(adminCreateRequest));
    }
  }

}
