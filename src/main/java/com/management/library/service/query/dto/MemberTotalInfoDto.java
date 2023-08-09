package com.management.library.service.query.dto;

import com.management.library.service.member.dto.MemberServiceReadDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberTotalInfoDto {

  @ApiModelProperty(example = "sehunkim")
  private String name;
  @ApiModelProperty(example = "100000001")
  private String memberCode;
  @ApiModelProperty(example = "980506")
  private String birthdayCode;
  @ApiModelProperty(example = "경상남도")
  private String legion;
  @ApiModelProperty(example = "김해시")
  private String city;
  @ApiModelProperty(example = "삼계로")
  private String street;
  @ApiModelProperty(example = "5")
  private String remainManagementRequestCount;
  @ApiModelProperty(example = "5")
  private String remainNewBookRequestCount;
  @ApiModelProperty(example = "2")
  private String remainRentalCount;
  @ApiModelProperty(example = "AVAILABLE")
  private String rentalStatus;

  @Builder
  private MemberTotalInfoDto(String name, String memberCode, String birthdayCode, String legion,
      String city, String street, String remainManagementRequestCount,
      String remainNewBookRequestCount, String remainRentalCount, String rentalStatus) {
    this.name = name;
    this.memberCode = memberCode;
    this.birthdayCode = birthdayCode;
    this.legion = legion;
    this.city = city;
    this.street = street;
    this.remainManagementRequestCount = remainManagementRequestCount;
    this.remainNewBookRequestCount = remainNewBookRequestCount;
    this.remainRentalCount = remainRentalCount;
    this.rentalStatus = rentalStatus;
  }

  public static MemberTotalInfoDto of(MemberServiceReadDto basicInfo, String managementCount,
      String newBookCount, String remainRentalCount, String rentalStatus) {
    return MemberTotalInfoDto.builder()
        .name(basicInfo.getName())
        .memberCode(basicInfo.getMemberCode())
        .birthdayCode(basicInfo.getBirthdayCode())
        .legion(basicInfo.getLegion())
        .city(basicInfo.getCity())
        .street(basicInfo.getStreet())
        .remainManagementRequestCount(managementCount)
        .remainNewBookRequestCount(newBookCount)
        .remainRentalCount(remainRentalCount)
        .rentalStatus(rentalStatus)
        .build();
  }
}
