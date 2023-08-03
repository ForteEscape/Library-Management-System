package com.management.library.service.admin;

import static com.management.library.exception.ErrorCode.ADMIN_ALREADY_EXISTS;
import static com.management.library.exception.ErrorCode.ADMIN_NOT_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.domain.admin.Administrator;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.service.admin.dto.AdminServiceCreateDto;
import com.management.library.service.admin.dto.AdminServiceCreateDto.Request;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AdminServiceTest {

  @Autowired
  private AdministratorRepository administratorRepository;

  @Autowired
  private AdminService adminService;

  @DisplayName("이메일과 패스워드 및 이름으로 관리자를 등록할 수 있다.")
  @Test
  public void createAdmin() throws Exception {
    // given
    Request request1 = getAdmin("admin1", "admin1@test.com", "12345");
    Request request2 = getAdmin("admin2", "admin2@test.com", "12345");
    Request request3 = getAdmin("admin3", "admin3@test.com", "12345");

    List<Request> requestList = List.of(request1, request2, request3);
    List<AdminServiceCreateDto.Response> result = new ArrayList<>();
    // when
    for (Request request : requestList) {
      result.add(adminService.createAdmin(request));
    }

    // then
    assertThat(result).hasSize(3)
        .extracting("name", "email")
        .containsExactlyInAnyOrder(
            tuple("admin1", "admin1@test.com"),
            tuple("admin2", "admin2@test.com"),
            tuple("admin3", "admin3@test.com")
        );
  }

  @DisplayName("중복된 이메일로 관리자 등록을 수행할 시 예외가 발생한다.")
  @Test
  public void createAdminWithDuplicateEmail() throws Exception {
    // given
    Request request1 = getAdmin("admin1", "admin1@test.com", "12345");
    Request request2 = getAdmin("admin3", "admin1@test.com", "12345");

    adminService.createAdmin(request1);

    // when
    // then
    assertThatThrownBy(() -> adminService.createAdmin(request2))
        .isInstanceOf(DuplicateException.class)
        .extracting("errorCode", "description")
        .contains(
            ADMIN_ALREADY_EXISTS, ADMIN_ALREADY_EXISTS.getDescription()
        );
  }

  @DisplayName("관리자 이메일로 관리자를 삭제할 수 있다.")
  @Test
  public void adminDelete() throws Exception {
    // given
    Request request1 = getAdmin("admin1", "admin1@test.com", "12345");
    Request request2 = getAdmin("admin2", "admin2@test.com", "12345");

    adminService.createAdmin(request1);
    adminService.createAdmin(request2);

    // when
    adminService.deleteAdmin(request1.getEmail());

    // then
    List<Administrator> all = administratorRepository.findAll();
    assertThat(all).hasSize(1)
        .extracting("name", "email")
        .contains(
            tuple("admin2", "admin2@test.com")
        );
  }

  @DisplayName("존재하지 않는 관리자 이메일로 관리자를 삭제할 시 예외가 발생한다.")
  @Test
  public void adminDeleteWithEmailNotExists() throws Exception {
    // given
    Request request1 = getAdmin("admin1", "admin1@test.com", "12345");
    Request request2 = getAdmin("admin2", "admin2@test.com", "12345");

    adminService.createAdmin(request1);
    adminService.createAdmin(request2);

    // when
    // then
    assertThatThrownBy(() -> adminService.deleteAdmin("admin3@test.com"))
        .isInstanceOf(NoSuchElementExistsException.class)
        .extracting("errorCode", "description")
        .contains(
            ADMIN_NOT_EXISTS, ADMIN_NOT_EXISTS.getDescription()
        );
  }

  private static Request getAdmin(String name, String email, String password) {
    return Request.builder()
        .name(name)
        .email(email)
        .password(password)
        .build();
  }
}