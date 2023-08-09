package com.management.library.controller.dto;

import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalControllerResponseDto {

  @ApiModelProperty(example = "1")
  private Long id;
  @ApiModelProperty(example = "book1")
  private String bookName;
  @ApiModelProperty(example = "100000001")
  private String memberCode;
  @ApiModelProperty(example = "2023-08-09")
  private LocalDate rentalStartDate;
  @ApiModelProperty(example = "2023-08-23")
  private LocalDate rentalEndDate;
  @ApiModelProperty(example = "AVAILABLE")
  private ExtendStatus extendStatus;
  @ApiModelProperty(example = "PROCEEDING")
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
