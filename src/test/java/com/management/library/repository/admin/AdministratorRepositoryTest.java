package com.management.library.repository.admin;

import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.domain.admin.Administrator;
import com.management.library.domain.type.Authority;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AdministratorRepositoryTest {

  @Autowired
  private AdministratorRepository administratorRepository;

  @DisplayName("회원이 존재하는지 확인할 수 있다.")
  @Test
  public void checkAdminExists() throws Exception {
    // given
    Administrator admin1 = getAdmin("admin1", "admin1@test.com", "password");
    Administrator admin2 = getAdmin("admin2", "admin2@test.com", "password");
    Administrator admin3 = getAdmin("admin3", "admin3@test.com", "password");

    administratorRepository.saveAll(List.of(admin1, admin2, admin3));

    // when
    boolean result1 = administratorRepository.existsByEmail("admin1@test.com");
    boolean result2 = administratorRepository.existsByEmail("admin2@test.com");
    boolean result3 = administratorRepository.existsByEmail("admin4@test.com");

    List<Boolean> resultSet = List.of(result1, result2, result3);

    // then
    assertThat(resultSet).hasSize(3)
        .containsExactly(true, true, false);
  }

  @DisplayName("회원을 삭제할 수 있다.")
  @Test
  public void deleteAdmin() throws Exception {
    // given
    Administrator admin1 = getAdmin("admin1", "admin1@test.com", "password");
    Administrator admin2 = getAdmin("admin2", "admin2@test.com", "password");
    Administrator admin3 = getAdmin("admin3", "admin3@test.com", "password");

    administratorRepository.saveAll(List.of(admin1, admin2, admin3));

    // when
    administratorRepository.deleteByEmail("admin2@test.com");

    // then
    assertThat(administratorRepository.existsByEmail("admin2@test.com")).isFalse();
  }

  public Administrator getAdmin(String name, String email, String password){
    return Administrator.builder()
        .name(name)
        .email(email)
        .password(password)
        .authority(Authority.ROLE_ADMIN)
        .build();
  }
}