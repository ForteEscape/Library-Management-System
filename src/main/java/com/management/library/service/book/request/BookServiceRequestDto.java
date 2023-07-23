package com.management.library.service.book.request;

import com.management.library.controller.book.request.BookControllerCreateRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookServiceRequestDto {

  private String title;
  private String author;
  private String publisher;
  private int publishedYear;
  private String location;
  private int typeCode;

  @Builder
  private BookServiceRequestDto(String title, String author, String publisher, int publishedYear,
      String location, int typeCode) {
    this.title = title;
    this.author = author;
    this.publisher = publisher;
    this.publishedYear = publishedYear;
    this.location = location;
    this.typeCode = typeCode;
  }

  public static BookServiceRequestDto of(BookControllerCreateRequestDto request){
    return BookServiceRequestDto.builder()
        .title(request.getTitle())
        .author(request.getAuthor())
        .publisher(request.getPublisher())
        .location(request.getLocation())
        .publishedYear(request.getPublishedYear())
        .typeCode(request.getTypeCode())
        .build();
  }
}
