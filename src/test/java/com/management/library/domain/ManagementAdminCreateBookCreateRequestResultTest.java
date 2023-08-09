package com.management.library.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.domain.admin.Administrator;
import com.management.library.domain.management.ManagementRequest;
import com.management.library.domain.management.ManagementRequestResult;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.type.Authority;
import com.management.library.domain.type.RequestStatus;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Slf4j
class ManagementAdminCreateBookCreateRequestResultTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("운영 개선 사항 요청 결과 엔티티 저장 테스트")
  void managementRequestResultSaveTest() {
    // given
    Member member = Member.builder()
        .name("memberA")
        .password("1234")
        .birthdayCode("980506")
        .address(new Address("서울", "강변", "강변길"))
        .memberCode("123456")
        .authority(Authority.ROLE_MEMBER)
        .build();

    em.persist(member);

    Administrator admin = Administrator.builder()
        .name("admin1")
        .authority(Authority.ROLE_ADMIN)
        .email("admin1@gmail.com")
        .password("1234")
        .build();

    em.persist(admin);

    ManagementRequest request = ManagementRequest.builder()
        .title("management request title")
        .content("management request content")
        .member(member)
        .requestStatus(RequestStatus.AWAIT)
        .build();

    em.persist(request);

    ManagementRequestResult requestResult = ManagementRequestResult.builder()
        .resultPostTitle("result title")
        .resultPostContent("result content")
        .managementRequest(request)
        .administrator(admin)
        .result(RequestStatus.ACCEPTED)
        .build();

    em.persist(requestResult);

    em.flush();
    em.clear();

    // when
    ManagementRequestResult result = em.find(ManagementRequestResult.class,
        requestResult.getId());

    // then
    assertThat(result.getAdministrator().getName())
        .isEqualTo(requestResult.getAdministrator().getName());

    assertThat(result.getResultPostTitle())
        .isEqualTo(requestResult.getResultPostTitle());

    assertThat(result.getResultPostContent())
        .isEqualTo(requestResult.getResultPostContent());

    assertThat(result.getResult()).isEqualTo(RequestStatus.ACCEPTED);
  }
}