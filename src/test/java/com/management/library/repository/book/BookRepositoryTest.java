package com.management.library.repository.book;

import static com.management.library.domain.type.BookStatus.*;
import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.book.Book;
import com.management.library.domain.book.BookInfo;
import com.management.library.controller.book.dto.BookSearchCond;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class BookRepositoryTest {

  @Autowired
  private BookRepository bookRepository;

  @DisplayName("도서 이름으로 도서를 조회할 수 있다.")
  @Test
  public void findByBookInfo_Title() throws Exception {
    // given
    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher", "location2", 2020, 130);

    bookRepository.saveAll(List.of(book1, book2, book3));

    // when
    Book book = bookRepository.findByTitleAndAuthor("spring", "park")
        .orElseThrow(() -> new IllegalArgumentException("해당 도서가 존재하지 않습니다."));

    // then
    assertThat(book)
        .extracting(Book::getBookInfo)
        .extracting(BookInfo::getTitle, BookInfo::getAuthor)
        .contains(
            "spring", "park"
        );
  }

  @DisplayName("도서를 찾을 수 없을 시 예외를 반환한다.")
  @Test
  public void findByTitleAndAuthorWithNoBook() throws Exception {
    // given
    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher", "location2", 2020, 130);

    bookRepository.saveAll(List.of(book1, book2, book3));

    // when
    // then
    assertThatThrownBy(() -> bookRepository.findByTitleAndAuthor("spring", "kim")
        .orElseThrow(() -> new IllegalArgumentException("해당 도서가 존재하지 않습니다.")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 도서가 존재하지 않습니다.");
  }

  @DisplayName("조건 없이 모든 도서를 검색할 수 있다. 한 번에 5개의 데이터씩 조회가 가능하다.")
  @Test
  public void bookSearchPagingTest() throws Exception {
    // given
    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher", "location2", 2020, 130);
    Book book4 = createBook("spring2", "park", "publisher1", "location3", 2020, 150);
    Book book5 = createBook("docker1", "lee", "publisher2", "location4", 2018, 110);
    Book book6 = createBook("docker2", "lee", "publisher2", "location5", 2020, 110);

    BookSearchCond cond = new BookSearchCond();

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    PageRequest pageRequest = PageRequest.of(0, 5);
    PageRequest pageRequest2 = PageRequest.of(1, 5);

    // when
    Page<Response> result = bookRepository.bookSearch(cond, pageRequest);
    List<Response> content = result.getContent();

    Page<Response> result2 = bookRepository.bookSearch(cond, pageRequest2);
    List<Response> content2 = result2.getContent();

    // then
    assertThat(content).hasSize(5)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("jpa1", "kim", "publisher", 2017, "location", 130, AVAILABLE),
            tuple("spring", "park", "publisher1", 2017, "location1", 150, AVAILABLE),
            tuple("jpa2", "kim", "publisher", 2020, "location2", 130, AVAILABLE),
            tuple("spring2", "park", "publisher1", 2020, "location3", 150, AVAILABLE),
            tuple("docker1", "lee", "publisher2", 2018, "location4", 110, AVAILABLE)
        );

    assertThat(content2).hasSize(1)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .contains(
            tuple("docker2", "lee", "publisher2", 2020, "location5", 110, AVAILABLE)
        );
  }

  @DisplayName("도서 이름으로 도서를 검색할 수 있다.")
  @Test
  public void bookSearchTestWithName() throws Exception {
    // given
    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher2", "location2", 2020, 130);

    BookSearchCond cond = new BookSearchCond();
    cond.setBookTitle("jpa");

    bookRepository.saveAll(List.of(book1, book2, book3));

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = bookRepository.bookSearch(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("jpa1", "kim", "publisher", 2017, "location", 130, AVAILABLE),
            tuple("jpa2", "kim", "publisher2", 2020, "location2", 130, AVAILABLE)
        );
  }

  @DisplayName("저자 이름으로 도서를 검색할 수 있다.")
  @Test
  public void bookSearchTestWithAuthor() throws Exception {
    // given
    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher2", "location2", 2020, 130);

    BookSearchCond cond = new BookSearchCond();
    cond.setBookAuthor("park");

    bookRepository.saveAll(List.of(book1, book2, book3));

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = bookRepository.bookSearch(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(1)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("spring", "park", "publisher1", 2017, "location1", 150, AVAILABLE)
        );
  }

  @DisplayName("출판사 이름으로 도서를 검색할 수 있다.")
  @Test
  public void bookSearchTestWithPublisher() throws Exception {
    // given
    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher2", "location2", 2020, 130);

    BookSearchCond cond = new BookSearchCond();
    cond.setPublisherName("publisher");

    bookRepository.saveAll(List.of(book1, book2, book3));

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = bookRepository.bookSearch(cond, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("jpa1", "kim", "publisher", 2017, "location", 130, AVAILABLE),
            tuple("spring", "park", "publisher", 2017, "location1", 150, AVAILABLE)
        );
  }

  @DisplayName("분류번호로 도서를 필터링할 수 있다. 한 번에 5개의 데이터를 가져올 수 있다.")
  @Test
  public void bookFilteringWithTypeCode() throws Exception {
    // given
    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher", "location2", 2020, 130);
    Book book4 = createBook("spring2", "park", "publisher1", "location3", 2020, 150);
    Book book5 = createBook("docker1", "lee", "publisher2", "location4", 2018, 210);
    Book book6 = createBook("docker2", "lee", "publisher2", "location5", 2020, 210);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> result = bookRepository.findAllByBookTypeCode(100, 200, pageRequest);
    List<Response> content = result.getContent();

    // then
    assertThat(content).hasSize(4)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("jpa1", "kim", "publisher", 2017, "location", 130, AVAILABLE),
            tuple("spring", "park", "publisher1", 2017, "location1", 150, AVAILABLE),
            tuple("jpa2", "kim", "publisher", 2020, "location2", 130, AVAILABLE),
            tuple("spring2", "park", "publisher1", 2020, "location3", 150, AVAILABLE)
        );
  }

  private Book createBook(String title, String author, String publisher, String location,
      int publishedYear, int typeCode) {
    BookInfo bookInfo = createBookInfo(title, author, publisher, location, publishedYear);

    return Book.builder()
        .bookInfo(bookInfo)
        .bookStatus(AVAILABLE)
        .typeCode(typeCode)
        .build();
  }

  private BookInfo createBookInfo(String title, String author, String publisher, String location,
      int publishedYear) {
    return BookInfo.builder()
        .title(title)
        .author(author)
        .publisher(publisher)
        .location(location)
        .publishedYear(publishedYear)
        .build();
  }
}