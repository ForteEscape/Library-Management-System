package com.management.library.service.review.dto;

import com.management.library.domain.book.BookReview;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookReviewOverviewDto {

  private Long id;
  private String bookTitle;
  private String reviewTitle;
  private int rate;

  @Builder
  private BookReviewOverviewDto(Long id, String bookTitle, String reviewTitle, int rate) {
    this.id = id;
    this.bookTitle = bookTitle;
    this.reviewTitle = reviewTitle;
    this.rate = rate;
  }

  public static BookReviewOverviewDto of(BookReview review) {
    return BookReviewOverviewDto.builder()
        .id(review.getId())
        .bookTitle(review.getBook().getBookInfo().getTitle())
        .reviewTitle(review.getReviewTitle())
        .rate(review.getRate())
        .build();
  }
}
