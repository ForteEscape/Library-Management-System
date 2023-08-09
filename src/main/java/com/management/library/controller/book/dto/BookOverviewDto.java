package com.management.library.controller.book.dto;

import com.management.library.service.book.dto.BookServiceCreateDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookOverviewDto {

  @ApiModelProperty(example = "1")
  private Long id;
  @ApiModelProperty(example = "book1")
  private String bookTitle;
  @ApiModelProperty(example = "author1")
  private String author;

  public BookOverviewDto(Long id, String bookTitle, String author) {
    this.id = id;
    this.bookTitle = bookTitle;
    this.author = author;
  }

  public static BookOverviewDto of(BookServiceCreateDto.Response response) {
    return new BookOverviewDto(response.getId(), response.getTitle(), response.getAuthor());
  }
}
