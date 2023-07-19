package com.management.library.dto;

import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRentalResponseDto {

  private String memberName;
  private String bookTitle;
  private LocalDateTime rentalStartDate;
  private LocalDateTime rentalEndDate;
  private ExtendStatus extendStatus;
  private RentalStatus rentalStatus;

  public static BookRentalResponseDto fromEntity(Rental rental) {
    return BookRentalResponseDto.builder()
        .bookTitle(rental.getBook().getBookInfo().getTitle())
        .memberName(rental.getMember().getName())
        .rentalStartDate(rental.getRentalStartDate())
        .rentalEndDate(rental.getRentalEndDate())
        .extendStatus(rental.getExtendStatus())
        .rentalStatus(rental.getRentalStatus())
        .build();
  }
}
