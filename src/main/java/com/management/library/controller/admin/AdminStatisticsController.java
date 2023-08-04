package com.management.library.controller.admin;

import com.management.library.controller.admin.dto.AdminControllerMonthlyResultDto;
import com.management.library.controller.admin.dto.AdminControllerYearlyResultDto;
import com.management.library.service.statistics.StatisticsService;
import com.management.library.service.statistics.dto.MonthlyResultDto;
import com.management.library.service.statistics.dto.YearResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/statistics")
public class AdminStatisticsController {

  private final StatisticsService statisticsService;

  @GetMapping("/monthly-rentals")
  public AdminControllerMonthlyResultDto getMonthlyRentalStatistics(
      @RequestParam("year") int year,
      @RequestParam("month") int month
  ) {
    MonthlyResultDto monthlyRentedCount = statisticsService.getMonthlyRentedCount(year, month);

    return AdminControllerMonthlyResultDto.of(monthlyRentedCount);
  }

  @GetMapping("/monthly-reviews")
  public AdminControllerMonthlyResultDto getMonthlyReviewStatistics(
      @RequestParam("year") int year,
      @RequestParam("month") int month
  ) {
    MonthlyResultDto monthlyRentedCount = statisticsService.getMonthlyReviewCount(year, month);

    return AdminControllerMonthlyResultDto.of(monthlyRentedCount);
  }

  @GetMapping("/monthly-unavailable-books")
  public AdminControllerMonthlyResultDto getMonthlyBookUnavailableStatistics(
      @RequestParam("year") int year,
      @RequestParam("month") int month
  ) {
    MonthlyResultDto monthlyRentedCount = statisticsService.getMonthlyBookUnavailableCount(year,
        month);

    return AdminControllerMonthlyResultDto.of(monthlyRentedCount);
  }

  @GetMapping("/yearly-rentals")
  public AdminControllerYearlyResultDto getYearlyRentalStatistics(@RequestParam("year") int year) {
    YearResultDto yearlyRentalData = statisticsService.getYearlyRentalCount(year);

    return AdminControllerYearlyResultDto.of(yearlyRentalData);
  }

  @GetMapping("/yearly-reviews")
  public AdminControllerYearlyResultDto getYearlyReviewStatistics(@RequestParam("year") int year) {
    YearResultDto yearlyRentalData = statisticsService.getYearlyReviewCount(year);

    return AdminControllerYearlyResultDto.of(yearlyRentalData);
  }

  @GetMapping("/yearly-unavailable-books")
  public AdminControllerYearlyResultDto getYearlyUnavailableBookStatistics(
      @RequestParam("year") int year) {
    YearResultDto yearlyRentalData = statisticsService.getYearlyBookUnavailableCount(year);

    return AdminControllerYearlyResultDto.of(yearlyRentalData);
  }
}
