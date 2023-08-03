package com.management.library.service.query.dto;

import com.management.library.service.member.dto.MemberServiceReadDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberTotalInfoDto {

  private String name;
  private String memberCode;
  private String birthdayCode;
  private String legion;
  private String city;
  private String street;
  private String remainManagementRequestCount;
  private String remainNewBookRequestCount;
  private String remainRentalCount;
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
