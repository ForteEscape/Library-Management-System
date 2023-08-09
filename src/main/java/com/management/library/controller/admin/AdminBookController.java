package com.management.library.controller.admin;

import com.management.library.controller.book.dto.BookControllerCreateDto;
import com.management.library.controller.book.dto.BookControllerCreateDto.BookCreateResponse;
import com.management.library.controller.book.dto.BookControllerUpdateDto;
import com.management.library.controller.book.dto.BookControllerUpdateDto.BookUpdateResponse;
import com.management.library.controller.book.dto.BookInfoResponseDto;
import com.management.library.controller.book.dto.BookOverviewDto;
import com.management.library.controller.book.dto.BookSearchCond;
import com.management.library.controller.dto.BookAllDto;
import com.management.library.controller.dto.PageInfo;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.book.dto.BookServiceResponseDto;
import com.management.library.service.book.dto.BookServiceUpdateDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"관리자 도서 관리 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/books")
public class AdminBookController {

  private final BookService bookService;

  // 도서 등록
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  @ApiOperation(value = "도서 정보 생성", notes = "새로운 도서를 생성한다.")
  public BookCreateResponse createBook(
      @RequestBody @Valid BookControllerCreateDto.BookCreateRequest bookCreateRequest
  ) {
    BookServiceCreateDto.Response newBook = bookService.createNewBook(
        BookServiceCreateDto.Request.of(bookCreateRequest));

    return BookCreateResponse.of(newBook);
  }

  // 도서 조회
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  @ApiOperation(value = "도서 목록 조회", notes = "도서 목록을 특정 조건으로 검색하여 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "bookTitle", value = "검색할 도서 이름"),
      @ApiImplicitParam(name = "bookAuthor", value = "검색할 도서 저자"),
      @ApiImplicitParam(name = "publisherName", value = "검색할 도서 출판사")
  })
  public ResponseEntity<?> getBookList(BookSearchCond cond, Pageable pageable) {
    Page<Response> responses = bookService.searchBook(cond, pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(), (int)
        responses.getTotalElements(), responses.getTotalPages());

    List<BookOverviewDto> result = responses.getContent().stream()
        .map(BookOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new BookAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 도서 조회
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{bookId}")
  @ApiOperation(value = "도서 단건 조회", notes = "도서의 id를 통해 도서 단건을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "bookId", value = "도서 id"),
  })
  public BookInfoResponseDto getBookDetail(@PathVariable("bookId") Long bookId) {
    BookServiceResponseDto bookData = bookService.getBookData(bookId);

    return BookInfoResponseDto.of(bookData);
  }

  // 도서 수정
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{bookId}")
  @ApiOperation(value = "도서 정보 수정", notes = "도서의 정보를 수정할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "bookId", value = "도서 id"),
  })
  public BookUpdateResponse updateBook(
      @PathVariable("bookId") Long bookId,
      @RequestBody @Valid BookControllerUpdateDto.BookUpdateRequest bookUpdateRequest
  ) {
    BookServiceUpdateDto.Response response = bookService.updateBookData(bookId,
        BookServiceUpdateDto.Request.of(bookUpdateRequest));

    return BookUpdateResponse.of(response);
  }

  // 도서 삭제
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{bookId}")
  @ApiOperation(value = "도서 정보 삭제", notes = "도서 정보를 삭제합니다. 불완전한 기능으로 사용하지 않는것을 권장합니다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "bookId", value = "도서 id"),
  })
  public String deleteBook(@PathVariable("bookId") Long bookId) {
    return bookService.deleteBookData(bookId);
  }
}
