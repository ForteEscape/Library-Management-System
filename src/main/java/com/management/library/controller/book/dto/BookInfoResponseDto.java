package com.management.library.controller.book.dto;

import com.management.library.domain.type.BookStatus;
import com.management.library.service.book.dto.BookServiceResponseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BookInfoResponseDto {

  @ApiModelProperty(example = "book1")
  private String title;
  @ApiModelProperty(example = "author1")
  private String author;
  @ApiModelProperty(example = "publisher1")
  private String publisher;
  @ApiModelProperty(example = "2015")
  private int publishedYear;
  @ApiModelProperty(example = "location1")
  private String location;
  @ApiModelProperty(example = "130")
  private int typeCode;
  @ApiModelProperty(example = "AVAILABLE")
  private BookStatus status;

  @Builder
  private BookInfoResponseDto(String title, String author, String publisher, int publishedYear,
      String location, int typeCode, BookStatus status) {
    this.title = title;
    this.author = author;
    this.publisher = publisher;
    this.publishedYear = publishedYear;
    this.location = location;
    this.typeCode = typeCode;
    this.status = status;
  }

  public static BookInfoResponseDto of(BookServiceResponseDto response) {
    return BookInfoResponseDto.builder()
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
