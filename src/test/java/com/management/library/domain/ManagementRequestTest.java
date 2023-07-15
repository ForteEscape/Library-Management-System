package com.management.library.domain;

import static com.management.library.domain.type.RequestStatus.*;
import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.type.MemberRentalStatus;
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
        .memberRentalStatus(MemberRentalStatus.RENTAL_AVAILABLE)
        .build();

    em.persist(member);

    ManagementRequest request = ManagementRequest.builder()
        .title("management request title")
        .content("management request content")
        .member(member)
        .requestStatus(STAND_BY)
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
    assertThat(result.getRequestStatus()).isEqualTo(STAND_BY);
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
        .memberRentalStatus(MemberRentalStatus.RENTAL_AVAILABLE)
        .build();

    em.persist(member);

    ManagementRequest request = ManagementRequest.builder()
        .title("management request title")
        .content("management request content")
        .member(member)
        .requestStatus(STAND_BY)
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