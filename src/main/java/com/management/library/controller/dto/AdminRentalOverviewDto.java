package com.management.library.controller.dto;

import com.management.library.domain.type.RentalStatus;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRentalOverviewDto {

  private Long id;
  private String bookTitle;
  private String memberCode;
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
