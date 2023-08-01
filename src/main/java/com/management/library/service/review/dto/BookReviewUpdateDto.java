package com.management.library.service.review.dto;

import com.management.library.controller.review.dto.ReviewUpdateControllerDto;
import com.management.library.domain.book.BookReview;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookReviewUpdateDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {

    private String updateReviewTitle;
    private String updateReviewContent;

    @Builder
    private Request(String updateReviewTitle, String updateReviewContent) {
      this.updateReviewTitle = updateReviewTitle;
      this.updateReviewContent = updateReviewContent;
    }

    public static Request of(ReviewUpdateControllerDto.Request request) {
      return Request.builder()
          .updateReviewTitle(request.getNewReviewTitle())
          .updateReviewContent(request.getNewReviewContent())
          .build();
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response {

    private String reviewTitle;
    private String reviewContent;
    private int rate;

    @Builder
    private Response(String reviewTitle, String reviewContent, int rate) {
      this.reviewTitle = reviewTitle;
      this.reviewContent = reviewContent;
      this.rate = rate;
    }

    public static Response of(BookReview review) {
      return Response.builder()
          .reviewTitle(review.getReviewTitle())
          .reviewContent(review.getReviewContent())
          .rate(review.getRate())
          .build();
    }
  }
}
