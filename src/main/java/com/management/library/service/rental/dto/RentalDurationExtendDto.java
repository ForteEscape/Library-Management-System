package com.management.library.service.rental.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RentalDurationExtendDto {

  private LocalDate rentalStartDate;
  private LocalDate rentalEndDate;

  @Builder
  public RentalDurationExtendDto(LocalDate rentalStartDate, LocalDate rentalEndDate) {
    this.rentalStartDate = rentalStartDate;
    this.rentalEndDate = rentalEndDate;
  }

  public static RentalDurationExtendDto of(LocalDate rentalStartDate, LocalDate rentalEndDate) {
    return RentalDurationExtendDto.builder()
        .rentalStartDate(rentalStartDate)
        .rentalEndDate(rentalEndDate)
        .build();
  }
}
