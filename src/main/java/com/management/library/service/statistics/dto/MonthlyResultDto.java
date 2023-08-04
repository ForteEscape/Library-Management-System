package com.management.library.service.statistics.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MonthlyResultDto {

  private int month;
  private int year;
  private String resultCount;

  public MonthlyResultDto(int year, int month, String resultCount) {
    this.month = month;
    this.year = year;
    this.resultCount = resultCount;
  }
}
