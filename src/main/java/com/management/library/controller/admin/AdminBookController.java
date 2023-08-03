package com.management.library.controller.admin;

import com.management.library.controller.book.dto.BookControllerCreateDto;
import com.management.library.controller.book.dto.BookControllerUpdateDto;
import com.management.library.controller.book.dto.BookInfoResponseDto;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceResponseDto;
import com.management.library.service.book.dto.BookServiceUpdateDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/books")
public class AdminBookController {

  private final BookService bookService;

  // 도서 등록
  @PostMapping
  public BookControllerCreateDto.Response createBook(
      @RequestBody @Valid BookControllerCreateDto.Request request
  ) {
    BookServiceCreateDto.Response newBook = bookService.createNewBook(
        BookServiceCreateDto.Request.of(request));

    return BookControllerCreateDto.Response.of(newBook);
  }

  // 도서 조회
  @GetMapping("/{bookId}")
  public BookInfoResponseDto getBookDetail(@PathVariable("bookId") Long bookId) {
    BookServiceResponseDto bookData = bookService.getBookData(bookId);

    return BookInfoResponseDto.of(bookData);
  }

  // 도서 수정
  @PutMapping("/{bookId}")
  public BookControllerUpdateDto.Response updateBook(
      @PathVariable("bookId") Long bookId,
      @RequestBody @Valid BookControllerUpdateDto.Request request
  ) {
    BookServiceUpdateDto.Response response = bookService.updateBookData(bookId,
        BookServiceUpdateDto.Request.of(request));

    return BookControllerUpdateDto.Response.of(response);
  }

  // 도서 삭제
  @DeleteMapping("/{bookId}")
  public String deleteBook(@PathVariable("bookId") Long bookId) {
    return bookService.deleteBookData(bookId);
  }
}
