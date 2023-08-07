package com.management.library.controller.admin.dto;

import com.management.library.service.statistics.dto.MonthlyResultDto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminControllerMonthlyResultDto {

  private int month;
  private int year;
  private String resultCount;

  public AdminControllerMonthlyResultDto(int month, int year, String resultCount) {
    this.month = month;
    this.year = year;
    this.resultCount = resultCount;
  }

  public static AdminControllerMonthlyResultDto of(MonthlyResultDto result) {
    return new AdminControllerMonthlyResultDto(result.getMonth(), result.getYear(),
        result.getResultCount());
  }
}
