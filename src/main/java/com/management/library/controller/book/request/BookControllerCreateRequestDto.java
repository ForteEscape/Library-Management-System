package com.management.library.controller.book.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookControllerCreateRequestDto {

  private String title;
  private String author;
  private String publisher;
  private int publishedYear;
  private String location;
  private int typeCode;

  @Builder
  private BookControllerCreateRequestDto(String title, String author, String publisher, int publishedYear,
      String location, int typeCode) {
    this.title = title;
    this.author = author;
    this.publisher = publisher;
    this.publishedYear = publishedYear;
    this.location = location;
    this.typeCode = typeCode;
  }
}
