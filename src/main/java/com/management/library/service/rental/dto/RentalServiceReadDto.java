package com.management.library.service.rental.dto;

import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalServiceReadDto {

  private String bookName;
  private LocalDateTime rentalStartDate;
  private LocalDateTime rentalEndDate;
  private ExtendStatus extendStatus;
  private RentalStatus rentalStatus;

  @Builder
  public RentalServiceReadDto(String bookName, LocalDateTime rentalStartDate,
      LocalDateTime rentalEndDate, ExtendStatus extendStatus, RentalStatus rentalStatus) {
    this.bookName = bookName;
    this.rentalStartDate = rentalStartDate;
    this.rentalEndDate = rentalEndDate;
    this.extendStatus = extendStatus;
    this.rentalStatus = rentalStatus;
  }

  public static RentalServiceReadDto of(Rental rental){
    return RentalServiceReadDto.builder()
        .bookName(rental.getBook().getBookInfo().getTitle())
        .rentalStartDate(rental.getRentalStartDate())
        .rentalEndDate(rental.getRentalEndDate())
        .extendStatus(rental.getExtendStatus())
        .rentalStatus(rental.getRentalStatus())
        .build();
  }
}
