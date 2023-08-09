package com.management.library.controller.book;

import com.management.library.controller.book.dto.BookInfoResponseDto;
import com.management.library.controller.book.dto.BookOverviewDto;
import com.management.library.controller.book.dto.BookSearchCond;
import com.management.library.controller.dto.ArrayResponseWrapper;
import com.management.library.controller.dto.BookAllDto;
import com.management.library.controller.dto.PageInfo;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.book.dto.BookServiceResponseDto;
import com.management.library.service.book.recommend.BookRecommendService;
import com.management.library.service.book.recommend.dto.BookRecommendResponseDto.RentedCount;
import com.management.library.service.book.recommend.dto.BookRecommendResponseDto.ReviewRate;
import com.management.library.service.review.BookReviewService;
import com.management.library.service.review.dto.BookReviewDetailDto;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"도서 조회 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

  private final BookService bookService;
  private final BookReviewService bookReviewService;
  private final BookRecommendService bookRecommendService;

  // 도서 조회
  @GetMapping
  @ApiOperation(value = "도서 조회 기능", notes = "특정 조건으로 도서를 검색할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "bookTitle", value = "검색할 도서 이름"),
      @ApiImplicitParam(name = "bookAuthor", value = "검색할 도서 저자"),
      @ApiImplicitParam(name = "publisherName", value = "검색할 출판사")
  })
  public ResponseEntity<?> getBookList(BookSearchCond cond, Pageable pageable) {
    Page<Response> resultPage = bookService.searchBook(cond, pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<BookOverviewDto> result = resultPage.getContent().stream()
        .map(BookOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new BookAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 도서 상세 조회
  @GetMapping("/{bookId}")
  @ApiOperation(value = "도서 단건 조회", notes = "도서 세부 정보를 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "bookId", value = "도서 id")
  })
  public BookInfoResponseDto getBook(@PathVariable("bookId") Long bookId) {
    BookServiceResponseDto bookData = bookService.getBookData(bookId);

    return BookInfoResponseDto.of(bookData);
  }

  // 해당 도서의 리뷰를 조회
  @GetMapping("/{bookId}/reviews")
  @ApiOperation(value = "도서 리뷰 조회", notes = "특정 도서에 등록된 리뷰들을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "bookId", value = "도서 id")
  })
  public Page<BookReviewOverviewDto> getBookReviews(@PathVariable("bookId") Long bookId,
      Pageable pageable) {
    return bookReviewService.getBookReviewList(bookId, pageable);
  }

  // 도서 리뷰 상세 조회
  @GetMapping("/{bookId}/reviews/{bookReviewId}")
  @ApiOperation(value = "도서 리뷰 상세 조회", notes = "특정 도서의 리뷰 상세 정보를 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "bookId", value = "도서 id"),
      @ApiImplicitParam(name = "bookReviewId", value = "도서 리뷰 id"),
  })
  public BookReviewDetailDto getBookReviewDetail(
      @PathVariable("bookId") Long bookId,
      @PathVariable("bookReviewId") Long bookReviewId
  ) {
    return bookReviewService.getReviewData(bookReviewId);
  }

  // 추천 도서 조회 - 대여 횟수 기준
  @GetMapping("/recommend-books/rented-count")
  @ApiOperation(value = "추천 도서 조회 - 대여 횟수", notes = "대여 횟수를 통한 추천 도서 기능")
  public ArrayResponseWrapper<List<RentedCount>> getRecommendBookWithRentedCount() {
    List<RentedCount> result = bookRecommendService.getRecommendBookListByRentalCount();

    ArrayResponseWrapper<List<RentedCount>> response = new ArrayResponseWrapper<>();
    response.setCount((long) result.size());
    response.setData(result);

    return response;
  }

  // 추천 도서 조회 - 도서 평점 기준
  @GetMapping("/recommend-books/book-review-rate")
  @ApiOperation(value = "추천 도서 조회 - 도서 평점", notes = "도서 평점을 통한 추천 도서 기능")
  public ArrayResponseWrapper<List<ReviewRate>> getRecommendBookWithReviewRate() {
    List<ReviewRate> result = bookRecommendService.getRecommendBookListByReviewRate();

    ArrayResponseWrapper<List<ReviewRate>> response = new ArrayResponseWrapper<>();
    response.setCount((long) result.size());
    response.setData(result);

    return response;
  }
}
