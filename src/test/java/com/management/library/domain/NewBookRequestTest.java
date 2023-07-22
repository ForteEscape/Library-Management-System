package com.management.library.domain;

import static com.management.library.domain.type.RequestStatus.*;
import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.newbook.NewBookRequest;
import com.management.library.domain.type.MemberRentalStatus;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Slf4j
class NewBookRequestTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("신 도서 요청 저장 테스트")
  void newBookRequestSaveTest() {
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

    NewBookRequest request = NewBookRequest.builder()
        .requestBookTitle("management request title")
        .requestContent("management request content")
        .member(member)
        .requestStatus(AWAIT)
        .build();

    em.persist(request);

    em.flush();
    em.clear();

    // when
    NewBookRequest result = em.find(NewBookRequest.class, request.getId());

    // then
    assertThat(result.getRequestBookTitle()).isEqualTo(request.getRequestBookTitle());
    assertThat(result.getRequestContent()).isEqualTo(request.getRequestContent());
    assertThat(result.getMember().getName()).isEqualTo(request.getMember().getName());
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
        .memberRentalStatus(MemberRentalStatus.RENTAL_AVAILABLE)
        .build();

    em.persist(member);

    NewBookRequest request = NewBookRequest.builder()
        .requestBookTitle("management request title")
        .requestContent("management request content")
        .member(member)
        .requestStatus(AWAIT)
        .build();

    em.persist(request);

    em.flush();
    em.clear();

    // when
    NewBookRequest result = em.find(NewBookRequest.class, request.getId());
    result.changeRequestStatus(ACCEPTED);

    em.flush();
    em.clear();

    NewBookRequest changedResult = em.find(NewBookRequest.class, request.getId());

    // then
    assertThat(changedResult.getRequestStatus()).isEqualTo(ACCEPTED);
  }
}