package com.management.library.service.rental.dto;

import com.management.library.controller.admin.dto.RentalRequestDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalBookInfoDto {

  private String bookTitle;
  private String author;

  @Builder
  private RentalBookInfoDto(String bookTitle, String author) {
    this.bookTitle = bookTitle;
    this.author = author;
  }

  public static RentalBookInfoDto of(RentalRequestDto request) {
    return RentalBookInfoDto.builder()
        .bookTitle(request.getBookTitle())
        .author(request.getAuthor())
        .build();
  }
}
