package com.management.library.controller.member;

import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.ReviewAllDto;
import com.management.library.controller.review.dto.ReviewUpdateControllerDto;
import com.management.library.service.review.BookReviewService;
import com.management.library.service.review.dto.BookReviewDetailDto;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import com.management.library.service.review.dto.BookReviewUpdateDto.Request;
import com.management.library.service.review.dto.BookReviewUpdateDto.Response;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-info/reviews")
public class MemberReviewController {

  private final BookReviewService bookReviewService;

  // 회원 리뷰 조회
  @PreAuthorize("hasRole('MEMBER')")
  @GetMapping
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
  public BookReviewDetailDto getMemberReviewDetail(@PathVariable("reviewId") Long reviewId) {
    return bookReviewService.getReviewData(reviewId);
  }

  // 회원 리뷰 수정
  @PreAuthorize("hasRole('MEMBER')")
  @PutMapping("/{reviewId}")
  public ReviewUpdateControllerDto.Response updateMemberReviewDetail(
      @RequestBody @Valid ReviewUpdateControllerDto.Request request,
      @PathVariable("reviewId") Long reviewId
  ){
    Response response = bookReviewService.updateReview(Request.of(request), reviewId);
    return ReviewUpdateControllerDto.Response.of(response);
  }
}
