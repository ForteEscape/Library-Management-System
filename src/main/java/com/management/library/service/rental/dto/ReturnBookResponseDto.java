package com.management.library.service.rental.dto;

import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.RentalStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReturnBookResponseDto {

  @ApiModelProperty(example = "book1")
  private String bookTitle;
  @ApiModelProperty(example = "author1")
  private String author;
  @ApiModelProperty(example = "RETURNED")
  private RentalStatus rentalStatus;
  @ApiModelProperty(example = "NOT-OVERDUE")
  private String overdueDate;

  @Builder
  public ReturnBookResponseDto(String bookTitle, String author, RentalStatus rentalStatus,
      String overdueDate) {
    this.bookTitle = bookTitle;
    this.author = author;
    this.rentalStatus = rentalStatus;
    this.overdueDate = overdueDate;
  }

  public static ReturnBookResponseDto of(Rental rental, RentalStatus rentalStatus, String overdueDate){
    return ReturnBookResponseDto.builder()
        .bookTitle(rental.getBook().getBookInfo().getTitle())
        .author(rental.getBook().getBookInfo().getAuthor())
        .rentalStatus(rentalStatus)
        .overdueDate(overdueDate)
        .build();
  }
}
