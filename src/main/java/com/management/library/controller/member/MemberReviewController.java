package com.management.library.controller.member;

import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.ReviewAllDto;
import com.management.library.controller.review.dto.ReviewUpdateControllerDto;
import com.management.library.controller.review.dto.ReviewUpdateControllerDto.BookReviewUpdateResponse;
import com.management.library.service.review.BookReviewService;
import com.management.library.service.review.dto.BookReviewDetailDto;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import com.management.library.service.review.dto.BookReviewUpdateDto.Request;
import com.management.library.service.review.dto.BookReviewUpdateDto.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"회원 전용 도서 리뷰 조회 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-info/reviews")
public class MemberReviewController {

  private final BookReviewService bookReviewService;

  // 회원 리뷰 조회
  @PreAuthorize("hasRole('MEMBER')")
  @GetMapping
  @ApiOperation(value = "회원 리뷰 조회", notes = "회원이 작성한 리뷰 조회")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속한 회원 정보")
  })
  public ResponseEntity<?> getMemberReviews(Principal principal, Pageable pageable) {
    Page<BookReviewOverviewDto> resultPage = bookReviewService.getMemberReviewDataList(
        principal.getName(), pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<BookReviewOverviewDto> content = resultPage.getContent();

    return new ResponseEntity<>(
        new ReviewAllDto<>(content, pageInfo),
        HttpStatus.OK
    );
  }

  // 회원 리뷰 상세 조회
  @PreAuthorize("hasRole('MEMBER')")
  @GetMapping("/{reviewId}")
  @ApiOperation(value = "회원 리뷰 상세 조회", notes = "회원이 작성한 리뷰 상세 조회")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "reviewId", value = "리뷰 id")
  })
  public BookReviewDetailDto getMemberReviewDetail(@PathVariable("reviewId") Long reviewId) {
    return bookReviewService.getReviewData(reviewId);
  }

  // 회원 리뷰 수정
  @PreAuthorize("hasRole('MEMBER')")
  @PutMapping("/{reviewId}")
  @ApiOperation(value = "회원 리뷰 수정", notes = "회원이 작성한 리뷰 수정")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "reviewId", value = "리뷰 id")
  })
  public BookReviewUpdateResponse updateMemberReviewDetail(
      @RequestBody @Valid ReviewUpdateControllerDto.BookReviewUpdateRequest bookReviewUpdateRequest,
      @PathVariable("reviewId") Long reviewId
  ){
    Response response = bookReviewService.updateReview(Request.of(bookReviewUpdateRequest), reviewId);
    return BookReviewUpdateResponse.of(response);
  }
}
