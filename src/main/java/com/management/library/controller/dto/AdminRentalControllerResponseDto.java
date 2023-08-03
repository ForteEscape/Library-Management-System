package com.management.library.controller.dto;

import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalControllerResponseDto {

  private Long id;
  private String bookName;
  private String memberCode;
  private LocalDate rentalStartDate;
  private LocalDate rentalEndDate;
  private ExtendStatus extendStatus;
  private RentalStatus rentalStatus;

  @Builder
  private AdminRentalControllerResponseDto(Long id, String bookName, String memberCode,
      LocalDate rentalStartDate, LocalDate rentalEndDate, ExtendStatus extendStatus,
      RentalStatus rentalStatus) {
    this.id = id;
    this.bookName = bookName;
    this.memberCode = memberCode;
    this.rentalStartDate = rentalStartDate;
    this.rentalEndDate = rentalEndDate;
    this.extendStatus = extendStatus;
    this.rentalStatus = rentalStatus;
  }

  public static AdminRentalControllerResponseDto of(RentalServiceResponseDto response) {
    return AdminRentalControllerResponseDto.builder()
        .id(response.getId())
        .bookName(response.getBookName())
        .memberCode(response.getMemberCode())
        .rentalStartDate(response.getRentalStartDate())
        .rentalEndDate(response.getRentalEndDate())
        .extendStatus(response.getExtendStatus())
        .rentalStatus(response.getRentalStatus())
        .build();
  }
}
