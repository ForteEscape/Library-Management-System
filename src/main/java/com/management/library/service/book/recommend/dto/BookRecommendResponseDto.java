package com.management.library.service.book.recommend.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;

public class BookRecommendResponseDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class RentedCount{
    private String bookTitle;
    private String rentedCount;

    @Builder
    private RentedCount(String bookTitle, String rentedCount) {
      this.bookTitle = bookTitle;
      this.rentedCount = rentedCount;
    }

    public static RentedCount of(TypedTuple<String> tuple){
      String bookTitle = tuple.getValue();
      int rentCount = tuple.getScore().intValue();

      return RentedCount.builder()
          .bookTitle(bookTitle)
          .rentedCount(String.valueOf(rentCount))
          .build();
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class ReviewRate{
    private String bookTitle;
    private String reviewRate;

    @Builder
    private ReviewRate(String bookTitle, String reviewRate) {
      this.bookTitle = bookTitle;
      this.reviewRate = reviewRate;
    }

    public static ReviewRate of(TypedTuple<String> tuple){
      return ReviewRate.builder()
          .bookTitle(tuple.getValue())
          .reviewRate(String.valueOf(tuple.getScore()))
          .build();
    }
  }

}
