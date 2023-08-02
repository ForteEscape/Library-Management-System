package com.management.library.controller.admin.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnBookDataDto {

  private String memberCode;
  private String bookTitle;
  private String author;

  @Builder
  public ReturnBookDataDto(String memberCode, String bookTitle, String author) {
    this.memberCode = memberCode;
    this.bookTitle = bookTitle;
    this.author = author;
  }
}
