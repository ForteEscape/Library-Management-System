package com.management.library.controller.review;

import com.management.library.controller.review.dto.BookReviewControllerDto;
import com.management.library.controller.review.dto.BookReviewControllerDto.BookReviewResponse;
import com.management.library.service.review.BookReviewService;
import com.management.library.service.review.dto.BookReviewServiceDto.Request;
import com.management.library.service.review.dto.BookReviewServiceDto.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"도서 리뷰 등록 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class BookReviewController {

  private final BookReviewService bookReviewService;

  // 리뷰 등록
  @PreAuthorize("hasRole('MEMBER')")
  @PostMapping
  @ApiOperation(value = "name", notes = "현재 접속 중인 회원 정보")
  public BookReviewResponse createBookReview(
      @RequestBody @Valid BookReviewControllerDto.BookReviewRequest bookReviewRequest,
      Principal principal
  ) {
    Response review = bookReviewService.createReview(
        bookReviewRequest.getBookTitle(), Request.of(bookReviewRequest),
        principal.getName());

    return BookReviewResponse.of(review);
  }
}
