package com.management.library.controller.admin.dto;

import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRentalResponseDto {

  @ApiModelProperty(example = "book1")
  private String bookName;
  @ApiModelProperty(example = "2023-08-09")
  private LocalDate rentalStartDate;
  @ApiModelProperty(example = "2023-08-23")
  private LocalDate rentalEndDate;
  @ApiModelProperty(example = "AVAILABLE")
  private ExtendStatus extendStatus;
  @ApiModelProperty(example = "PROCEEDING")
  private RentalStatus rentalStatus;

  @Builder
  public MemberRentalResponseDto(String bookName, LocalDate rentalStartDate,
      LocalDate rentalEndDate, ExtendStatus extendStatus, RentalStatus rentalStatus) {
    this.bookName = bookName;
    this.rentalStartDate = rentalStartDate;
    this.rentalEndDate = rentalEndDate;
    this.extendStatus = extendStatus;
    this.rentalStatus = rentalStatus;
  }

  public static MemberRentalResponseDto of(RentalServiceResponseDto rental) {
    return MemberRentalResponseDto.builder()
        .bookName(rental.getBookName())
        .rentalStartDate(rental.getRentalStartDate())
        .rentalEndDate(rental.getRentalEndDate())
        .extendStatus(rental.getExtendStatus())
        .rentalStatus(rental.getRentalStatus())
        .build();
  }
}
