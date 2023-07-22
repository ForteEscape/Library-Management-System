package com.management.library.controller.book;

import com.management.library.controller.book.request.BookControllerCreateRequestDto;
import com.management.library.controller.book.request.BookControllerUpdateRequestDto;
import com.management.library.service.book.BookService;
import com.management.library.service.book.request.BookServiceRequestDto;
import com.management.library.service.book.request.BookServiceUpdateRequestDto;
import com.management.library.service.book.response.BookServiceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

  private final BookService bookService;

  @PostMapping("/new-book")
  public BookServiceResponseDto addBook(@RequestBody BookControllerCreateRequestDto request) {
    return bookService.createNewBook(BookServiceRequestDto.of(request));
  }

  @PostMapping("/{bookId}/update")
  public BookServiceResponseDto updateBook(@PathVariable("bookId") Long id,
      @RequestBody BookControllerUpdateRequestDto request) {
    return bookService.updateBookData(id, BookServiceUpdateRequestDto.of(request));
  }

  @PostMapping("/{bookId}/delete")
  public String deleteBook(@PathVariable("bookId") Long id){
    return bookService.deleteBookData(id);
  }

  @GetMapping("/{bookId}")
  public BookServiceResponseDto getBook(@PathVariable("bookId") Long bookId) {
    return bookService.getBookData(bookId);
  }
}
