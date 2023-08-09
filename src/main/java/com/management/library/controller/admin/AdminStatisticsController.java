package com.management.library.controller.admin;

import com.management.library.controller.admin.dto.AdminControllerMonthlyResultDto;
import com.management.library.controller.admin.dto.AdminControllerYearlyResultDto;
import com.management.library.service.statistics.StatisticsService;
import com.management.library.service.statistics.dto.MonthlyResultDto;
import com.management.library.service.statistics.dto.YearResultDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"관리자 전용 통계 기능 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/statistics")
public class AdminStatisticsController {

  private final StatisticsService statisticsService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/monthly-rentals")
  @ApiOperation(value = "달 별 대여 통계 조회", notes = "달 별 대여 현황을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "year", value = "조회할 연도"),
      @ApiImplicitParam(name = "month", value = "조회할 달 수")
  })
  public AdminControllerMonthlyResultDto getMonthlyRentalStatistics(
      @RequestParam("year") int year,
      @RequestParam("month") int month
  ) {
    MonthlyResultDto monthlyRentedCount = statisticsService.getMonthlyRentedCount(year, month);

    return AdminControllerMonthlyResultDto.of(monthlyRentedCount);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/monthly-reviews")
  @ApiOperation(value = "달 별 리뷰 통계 조회", notes = "달 별 리뷰 현황을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "year", value = "조회할 연도"),
      @ApiImplicitParam(name = "month", value = "조회할 달 수")
  })
  public AdminControllerMonthlyResultDto getMonthlyReviewStatistics(
      @RequestParam("year") int year,
      @RequestParam("month") int month
  ) {
    MonthlyResultDto monthlyRentedCount = statisticsService.getMonthlyReviewCount(year, month);

    return AdminControllerMonthlyResultDto.of(monthlyRentedCount);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/monthly-unavailable-books")
  @ApiOperation(value = "달 별 도서 손망실 통계 조회", notes = "달 별 도서 손/망실 현황을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "year", value = "조회할 연도"),
      @ApiImplicitParam(name = "month", value = "조회할 달 수")
  })
  public AdminControllerMonthlyResultDto getMonthlyBookUnavailableStatistics(
      @RequestParam("year") int year,
      @RequestParam("month") int month
  ) {
    MonthlyResultDto monthlyRentedCount = statisticsService.getMonthlyBookUnavailableCount(year,
        month);

    return AdminControllerMonthlyResultDto.of(monthlyRentedCount);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/yearly-rentals")
  @ApiOperation(value = "연간 대여 통계 조회", notes = "연간 대여 현황을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "year", value = "조회할 연도")
  })
  public AdminControllerYearlyResultDto getYearlyRentalStatistics(@RequestParam("year") int year) {
    YearResultDto yearlyRentalData = statisticsService.getYearlyRentalCount(year);

    return AdminControllerYearlyResultDto.of(yearlyRentalData);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/yearly-reviews")
  @ApiOperation(value = "연간 리뷰 통계 조회", notes = "연간 라뷰 현황을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "year", value = "조회할 연도")
  })
  public AdminControllerYearlyResultDto getYearlyReviewStatistics(@RequestParam("year") int year) {
    YearResultDto yearlyRentalData = statisticsService.getYearlyReviewCount(year);

    return AdminControllerYearlyResultDto.of(yearlyRentalData);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/yearly-unavailable-books")
  @ApiOperation(value = "연간 도서 손망실 통계 조회", notes = "연간 도서 손/망실 현황을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "year", value = "조회할 연도")
  })
  public AdminControllerYearlyResultDto getYearlyUnavailableBookStatistics(
      @RequestParam("year") int year) {
    YearResultDto yearlyRentalData = statisticsService.getYearlyBookUnavailableCount(year);

    return AdminControllerYearlyResultDto.of(yearlyRentalData);
  }
}
