package com.management.library.controller.book;

import com.management.library.controller.book.dto.BookSearchCond;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceResponseDto;
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

  @GetMapping
  public Page<BookServiceCreateDto.Response> getBookList(BookSearchCond cond, Pageable pageable){
    return bookService.searchBook(cond, pageable);
  }

  @GetMapping("/{bookId}")
  public BookServiceResponseDto getBook(@PathVariable("bookId") Long bookId) {
    return bookService.getBookData(bookId);
  }

  @GetMapping("/{bookId}/reviews")
  public void getBookReviews(@PathVariable("bookId") Long bookId, Pageable pageable){

  }
}
