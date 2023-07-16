package com.management.library.domain;

import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.type.BookStatus;
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
class BookReviewTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("책 리뷰 엔티티 저장 테스트")
  void bookReviewTest() {
    // given
    Member memberA = Member.builder()
        .name("memberA")
        .build();

    em.persist(memberA);

    Book book = Book.builder()
        .bookInfo(new BookInfo("bookA", "author", "publisher", "4f", 2002))
        .bookStatus(BookStatus.AVAILABLE)
        .build();

    em.persist(book);

    BookReview bookReview = BookReview.builder()
        .reviewTitle("review")
        .content("content")
        .book(book)
        .member(memberA)
        .build();

    em.persist(bookReview);

    // when
    BookReview bookReview1 = em.find(BookReview.class, bookReview.getId());

    // then
    assertThat(bookReview1).isEqualTo(bookReview);
  }

  @Test
  @DisplayName("등록된 리뷰 수정 테스트")
  void changeReviewAndContentTest(){
    // given
    Member memberA = Member.builder()
        .name("memberA")
        .build();

    em.persist(memberA);

    Book book = Book.builder()
        .bookInfo(new BookInfo("bookA", "author", "publisher", "4f", 2002))
        .bookStatus(BookStatus.AVAILABLE)
        .build();

    em.persist(book);

    BookReview bookReview = BookReview.builder()
        .reviewTitle("review")
        .content("content")
        .book(book)
        .member(memberA)
        .build();

    em.persist(bookReview);

    em.flush();
    em.clear();

    // when
    BookReview bookReview1 = em.find(BookReview.class, bookReview.getId());
    bookReview1.changeReviewTitleAndContent("review Title2", "review content2");

    em.flush();
    em.clear();

    BookReview result = em.find(BookReview.class, bookReview.getId());

    // then
    assertThat(result.getReviewTitle()).isEqualTo("review Title2");
    assertThat(result.getContent()).isEqualTo("review content2");
    assertThat(result.getRate()).isEqualTo(bookReview.getRate());
  }
}