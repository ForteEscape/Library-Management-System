package com.management.library.service.book;

import static com.management.library.exception.ErrorCode.BOOK_ALREADY_EXISTS;
import static com.management.library.exception.ErrorCode.BOOK_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.INVALID_RANGE;

import com.management.library.domain.book.Book;
import com.management.library.controller.book.dto.BookSearchCond;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.InvalidArgumentException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.book.BookRepository;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.book.dto.BookServiceResponseDto;
import com.management.library.service.book.dto.BookServiceUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

  private final BookRepository bookRepository;
  private static final String SUCCESS = "success";

  /**
   * 새로운 book Entity 생성 만약 동일한 책(동일한 재목 및 저자)이 존재한다면 예외 발생 예외와 관련하여 커스텀 예외로 리펙토링 해야함
   *
   * @param request 요청 DTO
   * @return 결과 DTO
   */
  @Transactional
  public Response createNewBook(BookServiceCreateDto.Request request) {
    if (isBookPresent(request.getTitle(), request.getAuthor())) {
      throw new DuplicateException(BOOK_ALREADY_EXISTS);
    }
    Book book = bookRepository.save(Book.of(request));

    return Response.of(book);
  }

  /**
   * 도서 검색 기능 도서 검색은 제목, 저자, 출판사로 검색이 가능하다.
   *
   * @param cond     도서 검색 객체
   * @param pageable 결과 페이징 설정
   * @return 결과 객체
   */
  public Page<Response> searchBook(BookSearchCond cond, Pageable pageable) {
    return bookRepository.bookSearch(cond, pageable);
  }

  /**
   * 도서 분류 번호를 사용한 필터링 기능
   *
   * @param startCode 시작 분류 번호
   * @param endCode   끝 분류 번호
   * @param pageable  페이징 설정
   * @return 결과 객체
   */
  public Page<Response> searchBookByTypeCode(int startCode, int endCode,
      Pageable pageable) {

    if (isValidRange(startCode, endCode)) {
      throw new InvalidArgumentException(INVALID_RANGE);
    }

    return bookRepository.findAllByBookTypeCode(startCode, endCode, pageable);
  }

  private boolean isValidRange(int startCode, int endCode) {
    return startCode > endCode || startCode < 1 || startCode > 999 || endCode > 999;
  }

  /**
   * 도서 조회 기능
   *
   * @param bookId 조회할 도서 id
   * @return 도서 정보 DTO
   */
  @Cacheable(key = "#bookId", value = "book")
  public BookServiceResponseDto getBookData(Long bookId) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new NoSuchElementExistsException(BOOK_NOT_EXISTS));

    return BookServiceResponseDto.of(book);
  }

  /**
   * 도서 정보 수정 기능
   *
   * @param bookId  수정할 도서 id
   * @param request 수정 정보 DTO
   * @return 수정된 도서 정보 DTO
   */
  @CachePut(key = "#bookId", value = "book")
  @Transactional
  public BookServiceUpdateDto.Response updateBookData(Long bookId, BookServiceUpdateDto.Request request) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new NoSuchElementExistsException(BOOK_NOT_EXISTS));

    book.changeBookData(request);

    return BookServiceUpdateDto.Response.of(book);
  }

  @CacheEvict(key = "#bookId", value = "book")
  @Transactional
  public String deleteBookData(Long bookId) {
    Book book = bookRepository.findById(bookId)
        .orElseThrow(() -> new NoSuchElementExistsException(BOOK_NOT_EXISTS));

    bookRepository.delete(book);

    return SUCCESS;
  }

  private boolean isBookPresent(String title, String author) {
    return bookRepository.findByTitleAndAuthor(title, author).isPresent();
  }
}
