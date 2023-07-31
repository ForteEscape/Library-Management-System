package com.management.library.controller.admin.dto;

import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRentalDto {

  private String bookName;
  private LocalDate rentalStartDate;
  private LocalDate rentalEndDate;
  private ExtendStatus extendStatus;
  private RentalStatus rentalStatus;

  @Builder
  public MemberRentalDto(String bookName, LocalDate rentalStartDate,
      LocalDate rentalEndDate, ExtendStatus extendStatus, RentalStatus rentalStatus) {
    this.bookName = bookName;
    this.rentalStartDate = rentalStartDate;
    this.rentalEndDate = rentalEndDate;
    this.extendStatus = extendStatus;
    this.rentalStatus = rentalStatus;
  }

  public static MemberRentalDto of(RentalServiceResponseDto rental) {
    return MemberRentalDto.builder()
        .bookName(rental.getBookName())
        .rentalStartDate(rental.getRentalStartDate())
        .rentalEndDate(rental.getRentalEndDate())
        .extendStatus(rental.getExtendStatus())
        .rentalStatus(rental.getRentalStatus())
        .build();
  }
}
