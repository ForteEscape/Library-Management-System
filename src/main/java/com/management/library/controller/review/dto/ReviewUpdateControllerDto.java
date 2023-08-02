package com.management.library.controller.review.dto;

import com.management.library.service.review.dto.BookReviewUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReviewUpdateControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {

    private String newReviewTitle;
    private String newReviewContent;

    @Builder
    private Request(String newReviewTitle, String newReviewContent) {
      this.newReviewTitle = newReviewTitle;
      this.newReviewContent = newReviewContent;
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

    public static Response of(BookReviewUpdateDto.Response response) {
      return Response.builder()
          .reviewTitle(response.getReviewTitle())
          .reviewContent(response.getReviewContent())
          .rate(response.getRate())
          .build();
    }
  }
}
