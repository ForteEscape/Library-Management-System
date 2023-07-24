package com.management.library.service.book;

import static com.management.library.domain.type.BookStatus.*;
import static com.management.library.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.dto.ArrayResponseWrapper;
import com.management.library.dto.BookSearchCond;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.InvalidArgumentException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Request;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.book.dto.BookServiceResponseDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class BookServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private BookService bookService;

  @DisplayName("도서를 추가할 수 있다.")
  @Test
  public void addNewBook() {
    // given
    Request request1 = createRequest("book1", "author1", "publisher",
        2015, "location1", 135);

    Request request2 = createRequest("book2", "author2", "publisher2",
        2016, "location2", 130);

    // when
    Response response1 = bookService.createNewBook(request1);
    Response response2 = bookService.createNewBook(request2);

    // then
    assertThat(List.of(response1, response2))
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("book1", "author1", "publisher", 2015, "location1", 135, AVAILABLE),
            tuple("book2", "author2", "publisher2", 2016, "location2", 130, AVAILABLE)
        );
  }

  @DisplayName("이미 존재하는 도서를 추가할 수 없다.")
  @Test
  public void addNewBookWithDuplicateBook() {
    // given
    Request request1 = createRequest("book1", "author1", "publisher",
        2015, "location1", 135);

    Request request2 = createRequest("book1", "author1", "publisher",
        2015, "location1", 135);

    // when
    bookService.createNewBook(request1);

    // then
    assertThatThrownBy(() -> bookService.createNewBook(request2))
        .isInstanceOf(DuplicateException.class)
        .extracting("errorCode", "description")
        .contains(
            BOOK_ALREADY_EXISTS, BOOK_ALREADY_EXISTS.getDescription()
        );
  }

  @DisplayName("도서를 조회할 수 있다.")
  @Test
  public void getBookData() {
    // given
    Request request1 = createRequest("book1", "author1", "publisher",
        2015, "location1", 135);

    Request request2 = createRequest("book2", "author2", "publisher2",
        2016, "location2", 130);

    Response newBook1 = bookService.createNewBook(request1);
    Response newBook2 = bookService.createNewBook(request2);

    // when
    BookServiceResponseDto bookData1 = bookService.getBookData(newBook1.getId());
    BookServiceResponseDto bookData2 = bookService.getBookData(newBook2.getId());

    // then
    assertThat(List.of(bookData1, bookData2))
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("book1", "author1", "publisher", 2015, "location1", 135, AVAILABLE),
            tuple("book2", "author2", "publisher2", 2016, "location2", 130, AVAILABLE)
        );
  }

  @DisplayName("존재하지 않는 도서를 조회할 수 없다.")
  @Test
  public void getBookDataWithNotExists() {
    // when
    // then
    assertThatThrownBy(() -> bookService.getBookData(0L))
        .isInstanceOf(NoSuchElementExistsException.class)
        .extracting("errorCode", "description")
        .contains(
            BOOK_NOT_EXISTS, BOOK_NOT_EXISTS.getDescription()
        );
  }

  @DisplayName("모든 도서를 가져올 수 있다.")
  @Test
  public void searchBook() {
    // given
    Request request1 = createRequest("book1", "author1", "publisher1",
        2015, "location1", 130);
    Request request2 = createRequest("book2", "author2", "publisher2",
        2015, "location2", 135);
    Request request3 = createRequest("book3", "author3", "publisher3",
        2015, "location3", 140);
    Request request4 = createRequest("book4", "author4", "publisher4",
        2015, "location4", 145);
    Request request5 = createRequest("book5", "author5", "publisher5",
        2015, "location5", 150);
    Request request6 = createRequest("book6", "author6", "publisher6",
        2015, "location6", 155);

    List<Request> requests = List.of(request1, request2, request3, request4,
        request5, request6);

    for (Request request : requests) {
      bookService.createNewBook(request);
    }

    BookSearchCond cond = new BookSearchCond();
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    ArrayResponseWrapper<Page<Response>> result = bookService.searchBook(cond, pageRequest);
    Long count = result.getCount();
    List<Response> content = result.getData().getContent();

    // then
    assertThat(count).isEqualTo(6L);
    assertThat(content).hasSize(5)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("book1", "author1", "publisher1", 2015, "location1", 130, AVAILABLE),
            tuple("book2", "author2", "publisher2", 2015, "location2", 135, AVAILABLE),
            tuple("book3", "author3", "publisher3", 2015, "location3", 140, AVAILABLE),
            tuple("book4", "author4", "publisher4", 2015, "location4", 145, AVAILABLE),
            tuple("book5", "author5", "publisher5", 2015, "location5", 150, AVAILABLE)
        );
  }

  @DisplayName("도서 제목으로 검색해 가져올 수 있다.")
  @Test
  public void searchBookWithBookName() {
    // given
    Request request1 = createRequest("book1", "author1", "publisher1",
        2015, "location1", 130);
    Request request2 = createRequest("book2", "author2", "publisher2",
        2015, "location2", 135);
    Request request3 = createRequest("book3", "author3", "publisher3",
        2015, "location3", 140);
    Request request4 = createRequest("book4", "author4", "publisher4",
        2015, "location4", 145);
    Request request5 = createRequest("book5", "author5", "publisher5",
        2015, "location5", 150);
    Request request6 = createRequest("book6", "author6", "publisher6",
        2015, "location6", 155);

    List<Request> requests = List.of(request1, request2, request3, request4,
        request5, request6);

    for (Request request : requests) {
      bookService.createNewBook(request);
    }

    BookSearchCond cond = new BookSearchCond();
    cond.setBookTitle("book1");
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    ArrayResponseWrapper<Page<Response>> result = bookService.searchBook(cond,
        pageRequest);
    Long count = result.getCount();
    List<Response> content = result.getData().getContent();

    // then
    assertThat(count).isEqualTo(1L);
    assertThat(content).hasSize(1)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("book1", "author1", "publisher1", 2015, "location1", 130, AVAILABLE)
        );
  }

  @DisplayName("도서 작가 이름으로 검색해 가져올 수 있다.")
  @Test
  public void searchBookWithAuthorName() {
    // given
    Request request1 = createRequest("book1", "author1", "publisher1",
        2015, "location1", 130);
    Request request2 = createRequest("book2", "author1", "publisher2",
        2015, "location2", 135);
    Request request3 = createRequest("book3", "author1", "publisher3",
        2015, "location3", 140);
    Request request4 = createRequest("book4", "author1", "publisher4",
        2015, "location4", 145);
    Request request5 = createRequest("book5", "author5", "publisher5",
        2015, "location5", 150);
    Request request6 = createRequest("book6", "author6", "publisher6",
        2015, "location6", 155);

    List<Request> requests = List.of(request1, request2, request3, request4,
        request5, request6);

    for (Request request : requests) {
      bookService.createNewBook(request);
    }

    BookSearchCond cond = new BookSearchCond();
    cond.setBookAuthor("author1");
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    ArrayResponseWrapper<Page<Response>> result = bookService.searchBook(cond,
        pageRequest);
    Long count = result.getCount();
    List<Response> content = result.getData().getContent();

    // then
    assertThat(count).isEqualTo(4L);
    assertThat(content).hasSize(4)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("book1", "author1", "publisher1", 2015, "location1", 130, AVAILABLE),
            tuple("book2", "author1", "publisher2", 2015, "location2", 135, AVAILABLE),
            tuple("book3", "author1", "publisher3", 2015, "location3", 140, AVAILABLE),
            tuple("book4", "author1", "publisher4", 2015, "location4", 145, AVAILABLE)
        );
  }

  @DisplayName("도서 출판사 이름으로 검색해 가져올 수 있다.")
  @Test
  public void searchBookWithPublisherName() {
    // given
    Request request1 = createRequest("book1", "author1", "publisher1",
        2015, "location1", 130);
    Request request2 = createRequest("book2", "author2", "publisher1",
        2015, "location2", 135);
    Request request3 = createRequest("book3", "author3", "publisher1",
        2015, "location3", 140);
    Request request4 = createRequest("book4", "author4", "publisher1",
        2015, "location4", 145);
    Request request5 = createRequest("book5", "author5", "publisher5",
        2015, "location5", 150);
    Request request6 = createRequest("book6", "author6", "publisher6",
        2015, "location6", 155);

    List<Request> requests = List.of(request1, request2, request3, request4,
        request5, request6);

    for (Request request : requests) {
      bookService.createNewBook(request);
    }

    BookSearchCond cond = new BookSearchCond();
    cond.setPublisherName("publisher1");
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    ArrayResponseWrapper<Page<Response>> result = bookService.searchBook(cond,
        pageRequest);
    Long count = result.getCount();
    List<Response> content = result.getData().getContent();

    // then
    assertThat(count).isEqualTo(4L);
    assertThat(content).hasSize(4)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("book1", "author1", "publisher1", 2015, "location1", 130, AVAILABLE),
            tuple("book2", "author2", "publisher1", 2015, "location2", 135, AVAILABLE),
            tuple("book3", "author3", "publisher1", 2015, "location3", 140, AVAILABLE),
            tuple("book4", "author4", "publisher1", 2015, "location4", 145, AVAILABLE)
        );
  }

  @DisplayName("특정 분류 번호 번위에 있는 도서 목록들을 조회할 수 있다.")
  @Test
  public void searchBookByTypeCode() {
    // given
    Request request1 = createRequest("book1", "author1", "publisher1",
        2015, "location1", 130);
    Request request2 = createRequest("book2", "author2", "publisher2",
        2015, "location2", 135);
    Request request3 = createRequest("book3", "author3", "publisher3",
        2015, "location3", 240);
    Request request4 = createRequest("book4", "author4", "publisher4",
        2015, "location4", 245);
    Request request5 = createRequest("book5", "author5", "publisher5",
        2015, "location5", 350);
    Request request6 = createRequest("book6", "author6", "publisher6",
        2015, "location6", 355);

    List<Request> requests = List.of(request1, request2, request3, request4,
        request5, request6);

    for (Request request : requests) {
      bookService.createNewBook(request);
    }

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    ArrayResponseWrapper<Page<Response>> result = bookService.searchBookByTypeCode(
        100, 300, pageRequest);
    Long count = result.getCount();
    List<Response> content = result.getData().getContent();

    // then
    assertThat(count).isEqualTo(4L);
    assertThat(content).hasSize(4)
        .extracting("title", "author", "publisher", "publishedYear", "location", "typeCode",
            "status")
        .containsExactlyInAnyOrder(
            tuple("book1", "author1", "publisher1", 2015, "location1", 130, AVAILABLE),
            tuple("book2", "author2", "publisher2", 2015, "location2", 135, AVAILABLE),
            tuple("book3", "author3", "publisher3", 2015, "location3", 240, AVAILABLE),
            tuple("book4", "author4", "publisher4", 2015, "location4", 245, AVAILABLE)
        );
  }

  @DisplayName("잘못된 범위를 검색하는 경우 예외가 발생한다.")
  @Test
  public void searchBookByInvalidTypeCode() {
    // given
    Request request1 = createRequest("book1", "author1", "publisher1",
        2015, "location1", 130);
    Request request2 = createRequest("book2", "author2", "publisher2",
        2015, "location2", 135);
    Request request3 = createRequest("book3", "author3", "publisher3",
        2015, "location3", 240);
    Request request4 = createRequest("book4", "author4", "publisher4",
        2015, "location4", 245);
    Request request5 = createRequest("book5", "author5", "publisher5",
        2015, "location5", 350);
    Request request6 = createRequest("book6", "author6", "publisher6",
        2015, "location6", 355);

    List<Request> requests = List.of(request1, request2, request3, request4,
        request5, request6);

    for (Request request : requests) {
      bookService.createNewBook(request);
    }

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    // then
    assertThatThrownBy(() -> bookService.searchBookByTypeCode(300, 100, pageRequest))
        .isInstanceOf(InvalidArgumentException.class)
        .extracting("errorCode", "description")
        .contains(
            INVALID_RANGE, INVALID_RANGE.getDescription()
        );
  }

  private static Request createRequest(String title, String author, String publisher,
      int publishedYear, String location, int typeCode) {
    return BookServiceCreateDto.Request.builder()
        .title(title)
        .author(author)
        .publisher(publisher)
        .publishedYear(publishedYear)
        .location(location)
        .typeCode(typeCode)
        .build();
  }
}