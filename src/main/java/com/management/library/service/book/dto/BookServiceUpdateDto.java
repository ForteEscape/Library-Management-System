package com.management.library.service.book.dto;

import com.management.library.controller.book.dto.BookControllerCreateDto;
import com.management.library.controller.book.dto.BookControllerUpdateDto;
import com.management.library.domain.book.Book;
import com.management.library.domain.type.BookStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookServiceUpdateDto {

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

    public static Request of(BookControllerUpdateDto.Request request){
      return Request.builder()
          .title(request.getTitle())
          .author(request.getAuthor())
          .publisher(request.getPublisher())
          .location(request.getLocation())
          .publishedYear(request.getPublishedYear())
          .typeCode(request.getTypeCode())
          .build();
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
      this.location = location;
      this.status = status;
      this.typeCode = typeCode;
    }

    public static Response of(Book book){
      return Response.builder()
          .id(book.getId())
          .author(book.getBookInfo().getAuthor())
          .title(book.getBookInfo().getTitle())
          .publisher(book.getBookInfo().getPublisher())
          .publishedYear(book.getBookInfo().getPublishedYear())
          .location(book.getBookInfo().getLocation())
          .status(book.getBookStatus())
          .typeCode(book.getTypeCode())
          .build();
    }
  }
}
