package com.management.library.domain;

import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.AWAIT;
import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.domain.management.ManagementRequest;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Slf4j
class ManagementRequestTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("운영 개선 요청 엔티티 저장 테스트")
  void managementRequestSaveTest() {
    // given
    Member member = Member.builder()
        .name("memberA")
        .password("1234")
        .birthdayCode("980506")
        .address(new Address("서울", "강변", "강변길"))
        .memberCode("123456")
        .build();

    em.persist(member);

    ManagementRequest request = ManagementRequest.builder()
        .title("management request title")
        .content("management request content")
        .member(member)
        .requestStatus(AWAIT)
        .build();

    em.persist(request);

    em.flush();
    em.clear();

    // when
    ManagementRequest result = em.find(ManagementRequest.class, request.getId());

    // then
    assertThat(result.getTitle()).isEqualTo(request.getTitle());
    assertThat(result.getMember().getName()).isEqualTo(request.getMember().getName());
    assertThat(result.getContent()).isEqualTo(request.getContent());
    assertThat(result.getRequestStatus()).isEqualTo(AWAIT);
  }

  @Test
  @DisplayName("요청 상태 변경 메서드 테스트")
  void changeRequestStatus() {
    // given
    Member member = Member.builder()
        .name("memberA")
        .password("1234")
        .birthdayCode("980506")
        .address(new Address("서울", "강변", "강변길"))
        .memberCode("123456")
        .build();

    em.persist(member);

    ManagementRequest request = ManagementRequest.builder()
        .title("management request title")
        .content("management request content")
        .member(member)
        .requestStatus(AWAIT)
        .build();

    em.persist(request);

    em.flush();
    em.clear();

    // when
    ManagementRequest result = em.find(ManagementRequest.class, request.getId());
    result.changeRequestStatus(ACCEPTED);

    em.flush();
    em.clear();

    ManagementRequest changedRequest = em.find(ManagementRequest.class, request.getId());

    // then
    assertThat(changedRequest.getRequestStatus()).isEqualTo(ACCEPTED);
  }
}