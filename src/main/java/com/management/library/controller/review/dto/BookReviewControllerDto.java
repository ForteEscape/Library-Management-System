package com.management.library.controller.review.dto;

import com.management.library.service.review.dto.BookReviewServiceDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookReviewControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{
    private String bookTitle;
    private String reviewTitle;
    private String reviewContent;
    private int reviewRate;

    @Builder
    private Request(String bookTitle, String reviewTitle, String reviewContent, int reviewRate) {
      this.bookTitle = bookTitle;
      this.reviewTitle = reviewTitle;
      this.reviewContent = reviewContent;
      this.reviewRate = reviewRate;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response{
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

    public static Response of(BookReviewServiceDto.Response response){
      return Response.builder()
          .id(response.getId())
          .reviewTitle(response.getReviewTitle())
          .reviewContent(response.getReviewContent())
          .reviewRate(response.getReviewRate())
          .build();
    }
  }
}
