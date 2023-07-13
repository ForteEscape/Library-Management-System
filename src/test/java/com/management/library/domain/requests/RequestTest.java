package com.management.library.domain.requests;

import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.domain.Member;
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
class RequestTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("신간 요청 엔티티 테스트")
  void bookRequestTest() {
    // given
    Member member = Member.builder()
        .name("kim")
        .build();

    em.persist(member);

    BookRequest bookRequest = BookRequest.builder()
        .requestBookName("book A")
        .requestContent("book A required")
        .requestStatus(RequestStatus.STAND_BY)
        .member(member)
        .build();

    em.persist(bookRequest);

    // when
    BookRequest result = em.find(BookRequest.class, bookRequest.getId());

    // then
    assertThat(result).isEqualTo(bookRequest);
  }

  @Test
  @DisplayName("운영개선 요청 엔티티 테스트")
  void managementRequestTest() {
    // given
    Member member = Member.builder()
        .name("kim")
        .build();

    em.persist(member);

    ManagementRequest managementRequest = ManagementRequest.builder()
        .title("management request")
        .content("management request content")
        .requestStatus(RequestStatus.STAND_BY)
        .member(member)
        .build();

    em.persist(managementRequest);

    // when
    ManagementRequest result = em.find(ManagementRequest.class,
        managementRequest.getId());

    // then
    assertThat(result).isEqualTo(managementRequest);
  }

  @Test
  @DisplayName("도서관 요구 사항 요청 테스트 - 요구 사항 요청 상태 변경")
  void changeRequestStatusTest(){
    // given
    Member member = Member.builder()
        .name("kim")
        .build();

    em.persist(member);

    ManagementRequest managementRequest = ManagementRequest.builder()
        .title("management request")
        .content("management request content")
        .requestStatus(RequestStatus.STAND_BY)
        .member(member)
        .build();

    em.persist(managementRequest);

    BookRequest bookRequest = BookRequest.builder()
        .member(member)
        .requestBookName("request book 1")
        .requestContent("request book content")
        .requestStatus(RequestStatus.STAND_BY)
        .build();

    em.persist(bookRequest);

    em.flush();
    em.clear();

    // when
    ManagementRequest request = em.find(ManagementRequest.class,
        managementRequest.getId());

    BookRequest request2 = em.find(BookRequest.class, bookRequest.getId());

    request.changeRequestStatus(RequestStatus.ACCEPTED);
    request2.changeRequestStatus(RequestStatus.REFUSED);

    em.flush();
    em.clear();

    ManagementRequest result = em.find(ManagementRequest.class,
        managementRequest.getId());

    BookRequest result2 = em.find(BookRequest.class, bookRequest.getId());

    // then
    assertThat(result.getRequestStatus()).isEqualTo(RequestStatus.ACCEPTED);
    assertThat(result2.getRequestStatus()).isEqualTo(RequestStatus.REFUSED);
  }
}