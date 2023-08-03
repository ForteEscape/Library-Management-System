package com.management.library.controller.review.dto;

import com.management.library.service.review.dto.BookReviewUpdateDto;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReviewUpdateControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {

    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String newReviewTitle;
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
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
