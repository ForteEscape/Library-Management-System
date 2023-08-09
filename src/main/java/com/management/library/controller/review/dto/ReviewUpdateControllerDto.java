package com.management.library.controller.review.dto;

import com.management.library.service.review.dto.BookReviewUpdateDto;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class ReviewUpdateControllerDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class BookReviewUpdateRequest {

    @ApiModelProperty(example = "updateReviewTitle")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String newReviewTitle;
    @ApiModelProperty(example = "updateReviewContent")
    @NotBlank(message = "해당 부분은 비어있으면 안됩니다.")
    private String newReviewContent;

    @Builder
    private BookReviewUpdateRequest(String newReviewTitle, String newReviewContent) {
      this.newReviewTitle = newReviewTitle;
      this.newReviewContent = newReviewContent;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class BookReviewUpdateResponse {

    @ApiModelProperty(example = "updateReviewTitle")
    private String reviewTitle;
    @ApiModelProperty(example = "updateReviewContent")
    private String reviewContent;
    @ApiModelProperty(example = "5")
    private int rate;

    @Builder
    private BookReviewUpdateResponse(String reviewTitle, String reviewContent, int rate) {
      this.reviewTitle = reviewTitle;
      this.reviewContent = reviewContent;
      this.rate = rate;
    }

    public static BookReviewUpdateResponse of(BookReviewUpdateDto.Response response) {
      return BookReviewUpdateResponse.builder()
          .reviewTitle(response.getReviewTitle())
          .reviewContent(response.getReviewContent())
          .rate(response.getRate())
          .build();
    }
  }
}
