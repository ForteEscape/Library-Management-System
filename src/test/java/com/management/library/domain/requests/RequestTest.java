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
    Member member = Member.builder()
        .name("kim")
        .build();

    BookRequest bookRequest = BookRequest.builder()
        .requestBookName("book A")
        .requestContent("book A required")
        .requestStatus(RequestStatus.STAND_BY)
        .member(member)
        .build();

    em.persist(bookRequest);

    BookRequest result = em.find(BookRequest.class, bookRequest.getId());

    assertThat(result).isEqualTo(bookRequest);
  }

  @Test
  @DisplayName("운영개선 요청 엔티티 테스트")
  void managementRequestTest() {
    Member member = Member.builder()
        .name("kim")
        .build();

    ManagementRequest managementRequest = ManagementRequest.builder()
        .title("management request")
        .content("management request content")
        .requestStatus(RequestStatus.STAND_BY)
        .member(member)
        .build();

    em.persist(managementRequest);

    ManagementRequest result = em.find(ManagementRequest.class,
        managementRequest.getId());

    assertThat(result).isEqualTo(managementRequest);
  }
}