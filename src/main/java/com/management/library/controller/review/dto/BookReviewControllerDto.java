package com.management.library.controller.review.dto;

import com.management.library.service.review.dto.BookReviewServiceDto;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookReviewControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{

    @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
    private String bookTitle;
    @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
    private String reviewTitle;
    @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
    private String reviewContent;
    @Max(value = 5, message = "평점은 최대 5까지 지정할 수 있습니다.")
    @Min(value = 1, message = "평점은 최소 1까지 지정할 수 있습니다.")
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
