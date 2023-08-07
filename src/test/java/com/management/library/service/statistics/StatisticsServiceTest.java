package com.management.library.service.statistics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.service.statistics.dto.MonthlyResultDto;
import com.management.library.service.statistics.dto.YearResultDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class StatisticsServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Autowired
  private StatisticsService statisticsService;

  private static final String MONTHLY_RENTED_COUNT = "monthly-rented-count";
  private static final String MONTHLY_REVIEW_COUNT = "monthly-review-count";
  private static final String MONTHLY_BOOK_UNAVAILABLE_COUNT = "monthly-book-unavailable-count";
  private static final String YEARLY_RENTED_COUNT = "yearly-rented-count";
  private static final String YEARLY_REVIEW_COUNT = "yearly-review-count";
  private static final String YEARLY_BOOK_UNAVAILABLE_COUNT = "yearly-book-unavailable-count";

  @AfterEach
  void tearDown() {
    redisTemplate.delete(MONTHLY_RENTED_COUNT);
    redisTemplate.delete(MONTHLY_REVIEW_COUNT);
    redisTemplate.delete(MONTHLY_BOOK_UNAVAILABLE_COUNT);
    redisTemplate.delete(YEARLY_RENTED_COUNT);
    redisTemplate.delete(YEARLY_REVIEW_COUNT);
    redisTemplate.delete(YEARLY_BOOK_UNAVAILABLE_COUNT);
  }

  @DisplayName("월 간 대여 수를 가져올 수 있다.")
  @Test
  public void getMonthlyRentedCount() throws Exception {
    // given
    int year = 2023;
    int month = 8;
    redisTemplate.opsForHash().put(MONTHLY_RENTED_COUNT, year + "-" + month, "5");

    // when
    MonthlyResultDto monthlyRentedCount = statisticsService.getMonthlyRentedCount(year, month);

    // then
    assertThat(monthlyRentedCount)
        .extracting("year", "month", "resultCount")
        .contains(
            2023, 8, "5"
        );
  }

  @DisplayName("월 간 리뷰 수를 가져올 수 있다.")
  @Test
  public void getMonthlyReviewCount() throws Exception {
    // given
    int year = 2023;
    int month = 8;
    redisTemplate.opsForHash().put(MONTHLY_REVIEW_COUNT, year + "-" + month, "5");

    // when
    MonthlyResultDto monthlyRentedCount = statisticsService.getMonthlyReviewCount(year, month);

    // then
    assertThat(monthlyRentedCount)
        .extracting("year", "month", "resultCount")
        .contains(
            2023, 8, "5"
        );
  }

  @DisplayName("월 간 도서 손/망실 수를 가져올 수 있다.")
  @Test
  public void getMonthlyBookUnavailableCount() throws Exception {
    // given
    int year = 2023;
    int month = 8;
    redisTemplate.opsForHash().put(MONTHLY_BOOK_UNAVAILABLE_COUNT, year + "-" + month, "5");

    // when
    MonthlyResultDto monthlyRentedCount = statisticsService.getMonthlyBookUnavailableCount(year,
        month);

    // then
    assertThat(monthlyRentedCount)
        .extracting("year", "month", "resultCount")
        .contains(
            2023, 8, "5"
        );
  }

  @DisplayName("연 간 대여 수를 가져올 수 있다.")
  @Test
  public void getYearlyRentalCount() throws Exception {
    // given
    int year = 2023;

    for (int i = 1; i <= 12; i++){
      redisTemplate.opsForHash().put(MONTHLY_RENTED_COUNT, year + "-" + i, "3");
    }
    redisTemplate.opsForHash().put(YEARLY_RENTED_COUNT, "2023", "36");

    // when
    YearResultDto yearlyRentalCount = statisticsService.getYearlyRentalCount(year);

    // then
    assertThat(yearlyRentalCount)
        .extracting("year", "resultCount")
        .contains(
            2023, "36"
        );

    assertThat(yearlyRentalCount.getResult()).hasSize(12)
        .extracting("year", "month", "resultCount")
        .containsExactlyInAnyOrder(
            tuple(2023, 1, "3"),
            tuple(2023, 2, "3"),
            tuple(2023, 3, "3"),
            tuple(2023, 4, "3"),
            tuple(2023, 5, "3"),
            tuple(2023, 6, "3"),
            tuple(2023, 7, "3"),
            tuple(2023, 8, "3"),
            tuple(2023, 9, "3"),
            tuple(2023, 10, "3"),
            tuple(2023, 11, "3"),
            tuple(2023, 12, "3")
        );
  }

  @DisplayName("연 간 리뷰 수를 가져올 수 있다.")
  @Test
  public void getYearlyReviewCount() throws Exception {
    // given
    int year = 2023;

    for (int i = 1; i <= 12; i++){
      redisTemplate.opsForHash().put(MONTHLY_REVIEW_COUNT, year + "-" + i, "3");
    }
    redisTemplate.opsForHash().put(YEARLY_REVIEW_COUNT, "2023", "36");

    // when
    YearResultDto yearlyRentalCount = statisticsService.getYearlyReviewCount(year);

    // then
    assertThat(yearlyRentalCount)
        .extracting("year", "resultCount")
        .contains(
            2023, "36"
        );

    assertThat(yearlyRentalCount.getResult()).hasSize(12)
        .extracting("year", "month", "resultCount")
        .containsExactlyInAnyOrder(
            tuple(2023, 1, "3"),
            tuple(2023, 2, "3"),
            tuple(2023, 3, "3"),
            tuple(2023, 4, "3"),
            tuple(2023, 5, "3"),
            tuple(2023, 6, "3"),
            tuple(2023, 7, "3"),
            tuple(2023, 8, "3"),
            tuple(2023, 9, "3"),
            tuple(2023, 10, "3"),
            tuple(2023, 11, "3"),
            tuple(2023, 12, "3")
        );
  }

  @DisplayName("연 간 도서 손/망실 수를 가져올 수 있다.")
  @Test
  public void getYearlyBookUnavailableCount() throws Exception {
    // given
    int year = 2023;

    for (int i = 1; i <= 12; i++){
      redisTemplate.opsForHash().put(MONTHLY_BOOK_UNAVAILABLE_COUNT, year + "-" + i, "3");
    }
    redisTemplate.opsForHash().put(YEARLY_BOOK_UNAVAILABLE_COUNT, "2023", "36");

    // when
    YearResultDto yearlyRentalCount = statisticsService.getYearlyBookUnavailableCount(year);

    // then
    assertThat(yearlyRentalCount)
        .extracting("year", "resultCount")
        .contains(
            2023, "36"
        );

    assertThat(yearlyRentalCount.getResult()).hasSize(12)
        .extracting("year", "month", "resultCount")
        .containsExactlyInAnyOrder(
            tuple(2023, 1, "3"),
            tuple(2023, 2, "3"),
            tuple(2023, 3, "3"),
            tuple(2023, 4, "3"),
            tuple(2023, 5, "3"),
            tuple(2023, 6, "3"),
            tuple(2023, 7, "3"),
            tuple(2023, 8, "3"),
            tuple(2023, 9, "3"),
            tuple(2023, 10, "3"),
            tuple(2023, 11, "3"),
            tuple(2023, 12, "3")
        );
  }
}