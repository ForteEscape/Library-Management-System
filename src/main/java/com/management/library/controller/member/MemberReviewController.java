package com.management.library.controller.member;

import com.management.library.controller.review.dto.ReviewUpdateControllerDto;
import com.management.library.service.review.BookReviewService;
import com.management.library.service.review.dto.BookReviewDetailDto;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import com.management.library.service.review.dto.BookReviewUpdateDto.Request;
import com.management.library.service.review.dto.BookReviewUpdateDto.Response;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-info/reviews")
public class MemberReviewController {

  private final BookReviewService bookReviewService;

  // 회원 리뷰 조회
  @GetMapping
  public Page<BookReviewOverviewDto> getMemberReviews(Principal principal, Pageable pageable) {
    return bookReviewService.getMemberReviewDataList(principal.getName(), pageable);
  }

  // 회원 리뷰 상세 조회
  @GetMapping("/{reviewId}")
  public BookReviewDetailDto getMemberReviewDetail(@PathVariable("reviewId") Long reviewId) {
    return bookReviewService.getReviewData(reviewId);
  }

  // 회원 리뷰 수정
  @PutMapping("/{reviewId}")
  public ReviewUpdateControllerDto.Response updateMemberReviewDetail(
      @RequestBody ReviewUpdateControllerDto.Request request,
      @PathVariable("reviewId") Long reviewId
  ){
    Response response = bookReviewService.updateReview(Request.of(request), reviewId);
    return ReviewUpdateControllerDto.Response.of(response);
  }
}
