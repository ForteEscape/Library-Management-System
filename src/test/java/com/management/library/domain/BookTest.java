package com.management.library.domain;

import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.book.Book;
import com.management.library.domain.book.BookInfo;
import com.management.library.domain.type.BookStatus;
import com.management.library.dto.BookUpdateDto.Request;
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
class BookTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("도서 엔티티 테스트")
  void bookTest() {
    // given
    Book book = Book.builder()
        .bookInfo(new BookInfo("book", "author", "publisher", "location", 2002))
        .typeCode(835)
        .bookStatus(BookStatus.AVAILABLE)
        .build();

    em.persist(book);

    // when
    Book result = em.find(Book.class, book.getId());

    // then
    log.info("book created = {}", book.getCreatedAt());
    log.info("book modified = {}", book.getLastModifiedAt());

    assertThat(result).isEqualTo(book);
  }

  @Test
  @DisplayName("도서 엔티티 변경 - 도서 상태 변경 메서드")
  void changeBookStatusTest(){
    // given
    Book book = Book.builder()
        .bookInfo(new BookInfo("book", "author", "publisher", "location", 2002))
        .typeCode(835)
        .bookStatus(BookStatus.AVAILABLE)
        .build();

    em.persist(book);

    Book result = em.find(Book.class, book.getId());
    result.changeBookStatus(BookStatus.RENTAL);

    em.flush();
    em.clear();

    // when
    Book modifiedBook = em.find(Book.class, book.getId());

    // then
    assertThat(modifiedBook.getBookStatus()).isEqualTo(BookStatus.RENTAL);
  }

  @Test
  @DisplayName("도서 엔티티 변경 - 도서 정보 변경 메서드")
  void changeBookDataTest(){
    // given
    Book book = Book.builder()
        .bookInfo(new BookInfo("book", "author", "publisher", "location", 2002))
        .typeCode(835)
        .bookStatus(BookStatus.AVAILABLE)
        .build();

    em.persist(book);

    Request request = Request.builder()
        .title("book2")
        .author("author2")
        .publisher("publisher2")
        .location("location2")
        .publishedYear(2005)
        .typeCode(835)
        .build();

    book.changeBookData(request);

    em.flush();
    em.clear();

    // when
    Book result = em.find(Book.class, book.getId());

    // then
    assertThat(result.getBookInfo().getTitle()).isEqualTo("book2");
    assertThat(result.getBookInfo().getAuthor()).isEqualTo("author2");
    assertThat(result.getBookInfo().getPublisher()).isEqualTo("publisher2");
    assertThat(result.getBookInfo().getPublishedYear()).isEqualTo(2005);
    assertThat(result.getBookInfo().getLocation()).isEqualTo("location2");
    assertThat(result.getTypeCode()).isEqualTo(835);
  }
}