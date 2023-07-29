package com.management.library.service.rental.dto;

import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalServiceResponseDto {

  private String bookName;
  private LocalDate rentalStartDate;
  private LocalDate rentalEndDate;
  private ExtendStatus extendStatus;
  private RentalStatus rentalStatus;

  @Builder
  public RentalServiceResponseDto(String bookName, LocalDate rentalStartDate,
      LocalDate rentalEndDate, ExtendStatus extendStatus, RentalStatus rentalStatus) {
    this.bookName = bookName;
    this.rentalStartDate = rentalStartDate;
    this.rentalEndDate = rentalEndDate;
    this.extendStatus = extendStatus;
    this.rentalStatus = rentalStatus;
  }

  public static RentalServiceResponseDto of(Rental rental){
    return RentalServiceResponseDto.builder()
        .bookName(rental.getBook().getBookInfo().getTitle())
        .rentalStartDate(rental.getRentalStartDate())
        .rentalEndDate(rental.getRentalEndDate())
        .extendStatus(rental.getExtendStatus())
        .rentalStatus(rental.getRentalStatus())
        .build();
  }
}
