package com.management.library.service.book.response;

import com.management.library.domain.book.Book;
import com.management.library.domain.type.BookStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookServiceResponseDto {

  private Long id;
  private String title;
  private String author;
  private String publisher;
  private int publishedYear;
  private String location;
  private int typeCode;
  private BookStatus status;

  @Builder
  public BookServiceResponseDto(Long id, String title, String author, String publisher, int publishedYear,
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

  public static BookServiceResponseDto of(Book book){
    return BookServiceResponseDto.builder()
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
