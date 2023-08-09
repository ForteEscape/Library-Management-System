package com.management.library.service.review.dto;

import com.management.library.controller.review.dto.BookReviewControllerDto.BookReviewRequest;
import com.management.library.domain.book.BookReview;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookReviewServiceDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request {

    private String reviewTitle;
    private String reviewContent;
    private int reviewRate;

    @Builder
    private Request(String reviewTitle, String reviewContent, int reviewRate) {
      this.reviewTitle = reviewTitle;
      this.reviewContent = reviewContent;
      this.reviewRate = reviewRate;
    }

    public static Request of(BookReviewRequest bookReviewRequest) {
      return Request.builder()
          .reviewTitle(bookReviewRequest.getReviewTitle())
          .reviewContent(bookReviewRequest.getReviewContent())
          .reviewRate(bookReviewRequest.getReviewRate())
          .build();
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response {

    private Long id;
    private String reviewTitle;
    private String reviewContent;
    private int reviewRate;

    @Builder
    private Response(Long id, String reviewTitle, String reviewContent, int reviewRate) {
      this.id = id;
      this.reviewTitle = reviewTitle;
      this.reviewContent = reviewContent;
      this.reviewRate = reviewRate;
    }

    public static Response of(BookReview review) {
      return Response.builder()
          .id(review.getId())
          .reviewTitle(review.getReviewTitle())
          .reviewContent(review.getReviewContent())
          .reviewRate(review.getRate())
          .build();
    }
  }
}
