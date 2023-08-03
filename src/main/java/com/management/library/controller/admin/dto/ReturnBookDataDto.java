package com.management.library.controller.admin.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnBookDataDto {

  @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
  private String memberCode;
  @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
  private String bookTitle;
  @NotBlank(message = "해당 요소는 비어있으면 안됩니다.")
  private String author;

  @Builder
  public ReturnBookDataDto(String memberCode, String bookTitle, String author) {
    this.memberCode = memberCode;
    this.bookTitle = bookTitle;
    this.author = author;
  }
}
