package com.management.library.controller.admin.dto;

import com.management.library.service.statistics.dto.YearResultDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminControllerYearlyResultDto {

  private int year;
  private String resultCount;
  private List<AdminControllerMonthlyResultDto> result = new ArrayList<>();

  public AdminControllerYearlyResultDto(int year, String resultCount,
      List<AdminControllerMonthlyResultDto> result) {
    this.year = year;
    this.resultCount = resultCount;
    this.result = result;
  }

  public static AdminControllerYearlyResultDto of(YearResultDto result){
    List<AdminControllerMonthlyResultDto> data = result.getResult().stream()
        .map(AdminControllerMonthlyResultDto::of)
        .collect(Collectors.toList());

    return new AdminControllerYearlyResultDto(result.getYear(), result.getResultCount(), data);
  }
}
