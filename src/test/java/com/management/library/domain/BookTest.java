package com.management.library.domain;

import static org.junit.jupiter.api.Assertions.*;

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
class BookTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("책 엔티티 테스트")
  void bookTest(){
    Book book = Book.builder()
        .bookInfo(new BookInfo("book", "author", "publisher", "location", "isbn", 2002))
        .bookStatus(BookStatus.AVAILABLE)
        .typeCode(835)
        .build();

    em.persist(book);

    Book result = em.find(Book.class, book.getId());
    Assertions.assertThat(result).isEqualTo(book);
  }
}