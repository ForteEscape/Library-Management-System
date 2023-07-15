package com.management.library.domain;

import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.type.Authority;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class AdministratorTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("관리자 엔티티 저장 테스트")
  void administratorSaveTest() {
    // given
    Administrator admin = Administrator.builder()
        .authority(Authority.ROLE_ADMIN)
        .name("admin1")
        .email("admin1@gmail.com")
        .password("1234")
        .build();

    em.persist(admin);
    em.flush();
    em.clear();

    // when
    Administrator result = em.find(Administrator.class, admin.getId());

    // then
    assertThat(result.getName()).isEqualTo(admin.getName());
    assertThat(result.getAuthority()).isEqualTo(Authority.ROLE_ADMIN);
    assertThat(result.getEmail()).isEqualTo(admin.getEmail());
    assertThat(result.getPassword()).isEqualTo(admin.getPassword());
  }
}