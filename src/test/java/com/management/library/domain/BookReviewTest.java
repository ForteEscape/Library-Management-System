package com.management.library.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.domain.book.Book;
import com.management.library.domain.book.BookInfo;
import com.management.library.domain.book.BookReview;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.type.Authority;
import com.management.library.domain.type.BookStatus;
import com.management.library.service.review.dto.BookReviewUpdateDto.Request;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Slf4j
class BookReviewTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("등록된 리뷰 수정 테스트")
  void changeReviewAndContentTest() {
    // given
    Address address = getAddress("legion", "city", "street");
    Member memberA = getMember("kim", address, "10000001", "980101", Authority.ROLE_MEMBER, "1234");

    em.persist(memberA);

    Book book = Book.builder()
        .bookInfo(new BookInfo("bookA", "author", "publisher", "4f", 2002))
        .bookStatus(BookStatus.AVAILABLE)
        .build();

    em.persist(book);

    BookReview bookReview = BookReview.builder()
        .reviewTitle("review")
        .reviewContent("content")
        .book(book)
        .member(memberA)
        .build();

    em.persist(bookReview);

    em.flush();
    em.clear();

    // when
    BookReview bookReview1 = em.find(BookReview.class, bookReview.getId());
    Request updateRequest = Request.builder()
        .updateReviewTitle("review Title2")
        .updateReviewContent("review content2")
        .build();

    bookReview1.changeReviewTitleAndContent(updateRequest);

    em.flush();
    em.clear();

    BookReview result = em.find(BookReview.class, bookReview.getId());

    // then
    assertThat(result.getReviewTitle()).isEqualTo("review Title2");
    assertThat(result.getReviewContent()).isEqualTo("review content2");
    assertThat(result.getRate()).isEqualTo(bookReview.getRate());
  }

  private Address getAddress(String legion, String city, String street) {
    return Address.builder()
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }

  private static Member getMember(String name, Address address, String memberCode,
      String birthdayCode, Authority authority, String password) {
    return Member.builder()
        .name(name)
        .address(address)
        .memberCode(memberCode)
        .birthdayCode(birthdayCode)
        .authority(authority)
        .password(password)
        .build();
  }
}