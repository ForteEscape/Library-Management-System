package com.management.library.controller.review;

import com.management.library.controller.review.dto.BookReviewControllerDto;
import com.management.library.service.review.BookReviewService;
import com.management.library.service.review.dto.BookReviewServiceDto.Request;
import com.management.library.service.review.dto.BookReviewServiceDto.Response;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class BookReviewController {

  private final BookReviewService bookReviewService;

  // 리뷰 등록
  @PreAuthorize("hasRole('MEMBER')")
  @PostMapping
  public BookReviewControllerDto.Response createBookReview(
      @RequestBody @Valid BookReviewControllerDto.Request request,
      Principal principal
  ) {
    Response review = bookReviewService.createReview(request.getBookTitle(), Request.of(request),
        principal.getName());

    return BookReviewControllerDto.Response.of(review);
  }
}
