package com.management.library.service.review.dto;

import com.management.library.domain.book.BookReview;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookReviewDetailDto {

  private String bookTitle;
  private String reviewTitle;
  private String reviewContent;
  private int rate;

  @Builder
  private BookReviewDetailDto(String bookTitle, String reviewTitle, String reviewContent,
      int rate) {
    this.bookTitle = bookTitle;
    this.reviewTitle = reviewTitle;
    this.reviewContent = reviewContent;
    this.rate = rate;
  }

  public static BookReviewDetailDto of(BookReview review) {
    return BookReviewDetailDto.builder()
        .bookTitle(review.getBook().getBookInfo().getTitle())
        .reviewTitle(review.getReviewTitle())
        .reviewContent(review.getReviewContent())
        .rate(review.getRate())
        .build();
  }
}
