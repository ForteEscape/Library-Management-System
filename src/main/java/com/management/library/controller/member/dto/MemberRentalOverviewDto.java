package com.management.library.controller.member.dto;

import com.management.library.domain.type.RentalStatus;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRentalOverviewDto {

  @ApiModelProperty(example = "1")
  private Long id;
  @ApiModelProperty(example = "book1")
  private String bookTitle;
  @ApiModelProperty(example = "PROCEEDING")
  private RentalStatus rentalStatus;

  public MemberRentalOverviewDto(Long id, String bookTitle, RentalStatus rentalStatus) {
    this.id = id;
    this.bookTitle = bookTitle;
    this.rentalStatus = rentalStatus;
  }

  public static MemberRentalOverviewDto of(RentalServiceResponseDto response) {
    return new MemberRentalOverviewDto(response.getId(), response.getBookName(),
        response.getRentalStatus());
  }
}
