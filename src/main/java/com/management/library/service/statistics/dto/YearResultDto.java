package com.management.library.service.statistics.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YearResultDto {

  private int year;
  private String resultCount;
  private List<MonthlyResultDto> result = new ArrayList<>();

  public YearResultDto(int year, String resultCount, List<MonthlyResultDto> result) {
    this.year = year;
    this.resultCount = resultCount;
    this.result = result;
  }
}
