package com.management.library.service.book.dto;

import com.management.library.controller.book.dto.BookControllerCreateDto.BookCreateRequest;
import com.management.library.domain.book.Book;
import com.management.library.domain.type.BookStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class BookServiceCreateDto {

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
        String location, int typeCode) {
      this.title = title;
      this.author = author;
      this.publisher = publisher;
      this.publishedYear = publishedYear;
      this.location = location;
      this.typeCode = typeCode;
    }

    public static Request of(BookCreateRequest bookCreateRequest){
      return Request.builder()
          .title(bookCreateRequest.getTitle())
          .author(bookCreateRequest.getAuthor())
          .publisher(bookCreateRequest.getPublisher())
          .location(bookCreateRequest.getLocation())
          .publishedYear(bookCreateRequest.getPublishedYear())
          .typeCode(bookCreateRequest.getTypeCode())
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
    private String location;
    private int typeCode;
    private BookStatus status;

    @Builder
    public Response(Long id, String title, String author, String publisher, int publishedYear,
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
