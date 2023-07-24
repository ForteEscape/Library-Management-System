package com.management.library.controller.book.dto;

import com.management.library.domain.type.BookStatus;
import com.management.library.service.book.dto.BookServiceUpdateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookControllerUpdateDto {

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Request{
    private String title;
    private String author;
    private String publisher;
    private int publishedYear;
    private String location;
    private int typeCode;

    @Builder
    private Request(String title, String author, String publisher, int publishedYear,
        String location,
        int typeCode) {
      this.title = title;
      this.author = author;
      this.publisher = publisher;
      this.publishedYear = publishedYear;
      this.location = location;
      this.typeCode = typeCode;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class Response{
    private Long id;
    private String title;
    private String author;
    private String publisher;
    private int publishedYear;
    private BookStatus status;
    private String location;
    private int typeCode;

    @Builder
    private Response(Long id, String title, String author, String publisher, int publishedYear,
        BookStatus status, String location, int typeCode) {
      this.id = id;
      this.title = title;
      this.author = author;
      this.publisher = publisher;
      this.publishedYear = publishedYear;
      this.status = status;
      this.location = location;
      this.typeCode = typeCode;
    }

    public static Response of(BookServiceUpdateDto.Response response){
      return Response.builder()
          .id(response.getId())
          .author(response.getAuthor())
          .title(response.getTitle())
          .publisher(response.getPublisher())
          .publishedYear(response.getPublishedYear())
          .location(response.getLocation())
          .status(response.getStatus())
          .typeCode(response.getTypeCode())
          .build();
    }
  }
}
