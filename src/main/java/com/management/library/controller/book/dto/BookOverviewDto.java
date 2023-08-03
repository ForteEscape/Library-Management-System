package com.management.library.controller.book.dto;

import com.management.library.service.book.dto.BookServiceCreateDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookOverviewDto {

  private Long id;
  private String bookTitle;
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
