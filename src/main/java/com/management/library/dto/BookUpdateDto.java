package com.management.library.dto;

import com.management.library.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookUpdateDto {

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Request {

    private String title;
    private String author;
    private String publisher;
    private String location;
    private String isbn;
    private int publishedYear;
    private int typeCode;
  }

  @Getter
  @Setter
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  public static class Response {

    private String title;
    private String author;
    private String publisher;
    private String location;
    private String isbn;
    private int publishedYear;
    private int typeCode;

    public static Response fromEntity(Book book) {
      return Response.builder()
          .author(book.getBookInfo().getAuthor())
          .title(book.getBookInfo().getAuthor())
          .publisher(book.getBookInfo().getPublisher())
          .publishedYear(book.getBookInfo().getPublishedYear())
          .location(book.getBookInfo().getLocation())
          .isbn(book.getBookInfo().getIsbn())
          .typeCode(book.getTypeCode())
          .build();
    }
  }
}
