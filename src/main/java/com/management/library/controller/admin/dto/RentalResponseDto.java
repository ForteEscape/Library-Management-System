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
public class RentalResponseDto {

  private Long id;
  private String bookName;
  private LocalDate rentalStartDate;
  private LocalDate rentalEndDate;
  private ExtendStatus extendStatus;
  private RentalStatus rentalStatus;

  @Builder
  public RentalResponseDto(Long id, String bookName, LocalDate rentalStartDate,
      LocalDate rentalEndDate, ExtendStatus extendStatus, RentalStatus rentalStatus) {
    this.id = id;
    this.bookName = bookName;
    this.rentalStartDate = rentalStartDate;
    this.rentalEndDate = rentalEndDate;
    this.extendStatus = extendStatus;
    this.rentalStatus = rentalStatus;
  }

  public static RentalResponseDto of(RentalServiceResponseDto response){
    return RentalResponseDto.builder()
        .id(response.getId())
        .bookName(response.getBookName())
        .rentalStartDate(response.getRentalStartDate())
        .rentalEndDate(response.getRentalEndDate())
        .extendStatus(response.getExtendStatus())
        .rentalStatus(response.getRentalStatus())
        .build();
  }
}
