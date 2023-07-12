package com.management.library.domain;

import com.management.library.domain.type.BookStatus;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
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
  @DisplayName("책 리뷰 테스트")
  void bookReviewTest() {
    Member memberA = Member.builder()
        .name("memberA")
        .build();

    em.persist(memberA);

    Book book = Book.builder()
        .bookInfo(new BookInfo("bookA", "author", "publisher", "4f", "isbn", 2002))
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

    BookReview bookReview1 = em.find(BookReview.class, bookReview.getId());

    Assertions.assertThat(bookReview1).isEqualTo(bookReview);
  }
}