package com.management.library.dto;

import com.management.library.domain.book.BookReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookReviewUpdateDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    private String reviewTitle;
    private String content;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    private String reviewTitle;
    private String content;
    private String bookName;
    private String memberName;
    private int rate;

    public static Response fromEntity(BookReview bookReview) {
      return Response.builder()
          .bookName(bookReview.getBook().getBookInfo().getTitle())
          .memberName(bookReview.getMember().getName())
          .reviewTitle(bookReview.getReviewTitle())
          .content(bookReview.getContent())
          .build();
    }
  }
}
