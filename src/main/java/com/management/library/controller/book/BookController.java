package com.management.library.controller.book;

import static com.management.library.controller.book.dto.BookControllerCreateDto.*;

import com.management.library.controller.book.dto.BookControllerCreateDto;
import com.management.library.controller.book.dto.BookControllerUpdateDto;
import com.management.library.controller.book.dto.BookSearchCond;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceUpdateDto;
import com.management.library.service.book.dto.BookServiceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/books")
public class BookController {

  private final BookService bookService;

  @PostMapping
  public Response addBook(@RequestBody BookControllerCreateDto.Request request) {
    BookServiceCreateDto.Response result = bookService.createNewBook(
        BookServiceCreateDto.Request.of(request));
    return Response.of(result);
  }

  @PutMapping("/{bookId}/update")
  public BookControllerUpdateDto.Response updateBook(@PathVariable("bookId") Long id,
      @RequestBody BookControllerUpdateDto.Request request) {

    BookServiceUpdateDto.Response result = bookService.updateBookData(id,
        BookServiceUpdateDto.Request.of(request));
    return BookControllerUpdateDto.Response.of(result);
  }

  @GetMapping
  public Page<BookServiceCreateDto.Response> getBookList(BookSearchCond cond, Pageable pageable){
    return bookService.searchBook(cond, pageable);
  }

  @DeleteMapping("/{bookId}")
  public String deleteBook(@PathVariable("bookId") Long id) {
    return bookService.deleteBookData(id);
  }

  @GetMapping("/{bookId}")
  public BookServiceResponseDto getBook(@PathVariable("bookId") Long bookId) {
    return bookService.getBookData(bookId);
  }
}
