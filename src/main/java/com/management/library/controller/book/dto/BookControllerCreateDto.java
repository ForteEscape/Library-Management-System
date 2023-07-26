package com.management.library.controller.book.dto;

import com.management.library.domain.type.BookStatus;
import com.management.library.service.book.dto.BookServiceCreateDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookControllerCreateDto {

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
    private String location;
    private int typeCode;
    private BookStatus status;

    @Builder
    private Response(Long id, String title, String author, String publisher, int publishedYear,
        String location, int typeCode, BookStatus status) {
      this.id = id;
      this.title = title;
      this.author = author;
      this.publisher = publisher;
      this.publishedYear = publishedYear;
      this.location = location;
      this.typeCode = typeCode;
      this.status = status;
    }

    public static Response of(BookServiceCreateDto.Response response){
      return Response.builder()
          .id(response.getId())
          .title(response.getTitle())
          .author(response.getAuthor())
          .publisher(response.getPublisher())
          .publishedYear(response.getPublishedYear())
          .location(response.getLocation())
          .typeCode(response.getTypeCode())
          .status(response.getStatus())
          .build();
    }
  }

}
