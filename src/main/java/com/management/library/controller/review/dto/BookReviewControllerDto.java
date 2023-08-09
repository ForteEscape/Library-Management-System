package com.management.library.controller.review.dto;

import com.management.library.service.review.dto.BookReviewServiceDto;
import io.swagger.annotations.ApiModelProperty;
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
  public static class BookReviewRequest {

    @ApiModelProperty(example = "book1")
    @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
    private String bookTitle;
    @ApiModelProperty(example = "reviewTitle")
    @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
    private String reviewTitle;
    @ApiModelProperty(example = "reviewContent")
    @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
    private String reviewContent;
    @ApiModelProperty(example = "5")
    @Max(value = 5, message = "평점은 최대 5까지 지정할 수 있습니다.")
    @Min(value = 1, message = "평점은 최소 1까지 지정할 수 있습니다.")
    private int reviewRate;

    @Builder
    private BookReviewRequest(String bookTitle, String reviewTitle, String reviewContent,
        int reviewRate) {
      this.bookTitle = bookTitle;
      this.reviewTitle = reviewTitle;
      this.reviewContent = reviewContent;
      this.reviewRate = reviewRate;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class BookReviewResponse {

    @ApiModelProperty(example = "1")
    private Long id;
    @ApiModelProperty(example = "reviewTitle")
    private String reviewTitle;
    @ApiModelProperty(example = "reviewContent")
    private String reviewContent;
    @ApiModelProperty(example = "5")
    private int reviewRate;

    @Builder
    private BookReviewResponse(Long id, String reviewTitle, String reviewContent, int reviewRate) {
      this.id = id;
      this.reviewTitle = reviewTitle;
      this.reviewContent = reviewContent;
      this.reviewRate = reviewRate;
    }

    public static BookReviewResponse of(BookReviewServiceDto.Response response) {
      return BookReviewResponse.builder()
          .id(response.getId())
          .reviewTitle(response.getReviewTitle())
          .reviewContent(response.getReviewContent())
          .reviewRate(response.getReviewRate())
          .build();
    }
  }
}
