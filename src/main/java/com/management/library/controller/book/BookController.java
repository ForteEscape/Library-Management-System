package com.management.library.controller.book;

import com.management.library.controller.book.dto.BookSearchCond;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceResponseDto;
import com.management.library.service.review.BookReviewService;
import com.management.library.service.review.dto.BookReviewDetailDto;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

  private final BookService bookService;
  private final BookReviewService bookReviewService;

  @GetMapping
  public Page<BookServiceCreateDto.Response> getBookList(BookSearchCond cond, Pageable pageable) {
    return bookService.searchBook(cond, pageable);
  }

  @GetMapping("/{bookId}")
  public BookServiceResponseDto getBook(@PathVariable("bookId") Long bookId) {
    return bookService.getBookData(bookId);
  }

  // 해당 도서의 리뷰를 조회
  @GetMapping("/{bookId}/reviews")
  public Page<BookReviewOverviewDto> getBookReviews(@PathVariable("bookId") Long bookId,
      Pageable pageable) {
    return bookReviewService.getBookReviewList(bookId, pageable);
  }

  // 도서 리뷰 상세 조회
  @GetMapping("/{bookId}/reviews/{bookReviewId}")
  public BookReviewDetailDto getBookReviewDetail(
      @PathVariable("bookId") Long bookId,
      @PathVariable("bookReviewId") Long bookReviewId
  ){
    return bookReviewService.getReviewData(bookReviewId);
  }
}
