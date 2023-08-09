package com.management.library.controller.dto;

import com.management.library.domain.type.RentalStatus;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalOverviewDto {

  @ApiModelProperty(example = "1")
  private Long id;
  @ApiModelProperty(example = "book1")
  private String bookTitle;
  @ApiModelProperty(example = "100000001")
  private String memberCode;
  @ApiModelProperty(example = "PROCEEDING")
  private RentalStatus rentalStatus;

  @Builder
  private AdminRentalOverviewDto(Long id, String bookTitle, String memberCode,
      RentalStatus rentalStatus) {
    this.id = id;
    this.bookTitle = bookTitle;
    this.memberCode = memberCode;
    this.rentalStatus = rentalStatus;
  }

  public static AdminRentalOverviewDto of(RentalServiceResponseDto response){
    return AdminRentalOverviewDto.builder()
        .id(response.getId())
        .bookTitle(response.getBookName())
        .memberCode(response.getMemberCode())
        .rentalStatus(response.getRentalStatus())
        .build();
  }
}
