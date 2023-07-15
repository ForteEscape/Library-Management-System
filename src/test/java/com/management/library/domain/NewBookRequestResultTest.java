package com.management.library.domain;

import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.STAND_BY;
import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.domain.type.Authority;
import com.management.library.domain.type.MemberRentalStatus;
import com.management.library.domain.type.RequestStatus;
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
class NewBookRequestResultTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("신 도서 반입 요청 결과 엔티티 저장 테스트")
  void newBookRequestResultSaveTest() {
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

    Administrator admin = Administrator.builder()
        .name("admin1")
        .authority(Authority.ROLE_ADMIN)
        .email("admin1@gmail.com")
        .password("1234")
        .build();

    em.persist(admin);

    NewBookRequest request = NewBookRequest.builder()
        .requestBookTitle("management request title")
        .requestContent("management request content")
        .member(member)
        .requestStatus(STAND_BY)
        .build();

    em.persist(request);

    NewBookRequestResult requestResult = NewBookRequestResult.builder()
        .resultPostTitle("result title")
        .resultPostContent("result content")
        .newBookRequest(request)
        .administrator(admin)
        .result(ACCEPTED)
        .build();

    em.persist(requestResult);

    em.flush();
    em.clear();

    // when
    NewBookRequestResult result = em.find(NewBookRequestResult.class,
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