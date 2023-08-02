package com.management.library.controller.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalRequestDto {

  private String bookTitle;
  private String author;

  @Builder
  public RentalRequestDto(String bookTitle, String author) {
    this.bookTitle = bookTitle;
    this.author = author;
  }
}
