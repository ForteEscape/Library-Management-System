package com.management.library.service.statistics;

import com.management.library.exception.ErrorCode;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.service.statistics.dto.MonthlyResultDto;
import com.management.library.service.statistics.dto.YearResultDto;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatisticsService {

  private final RedisTemplate<String, String> redisTemplate;

  private static final String MONTHLY_RENTED_COUNT = "monthly-rented-count";
  private static final String MONTHLY_REVIEW_COUNT = "monthly-review-count";
  private static final String MONTHLY_BOOK_UNAVAILABLE_COUNT = "monthly-book-unavailable-count";
  private static final String YEARLY_RENTED_COUNT = "yearly-rented-count";
  private static final String YEARLY_REVIEW_COUNT = "yearly-review-count";
  private static final String YEARLY_BOOK_UNAVAILABLE_COUNT = "yearly-book-unavailable-count";

  public MonthlyResultDto getMonthlyRentedCount(int year, int month){
    String key = year + "-" + month;
    Object o = redisTemplate.opsForHash().get(MONTHLY_RENTED_COUNT, key);

    if (o == null){
      throw new NoSuchElementExistsException(ErrorCode.DATA_NOT_EXISTS);
    }

    return new MonthlyResultDto(year, month, String.valueOf(o));
  }

  public MonthlyResultDto getMonthlyReviewCount(int year, int month){
    String key = year + "-" + month;
    Object o = redisTemplate.opsForHash().get(MONTHLY_REVIEW_COUNT, key);

    if (o == null){
      throw new NoSuchElementExistsException(ErrorCode.DATA_NOT_EXISTS);
    }

    return new MonthlyResultDto(year, month, String.valueOf(o));
  }

  public MonthlyResultDto getMonthlyBookUnavailableCount(int year, int month){
    String key = year + "-" + month;
    Object o = redisTemplate.opsForHash().get(MONTHLY_BOOK_UNAVAILABLE_COUNT, key);

    if (o == null){
      throw new NoSuchElementExistsException(ErrorCode.DATA_NOT_EXISTS);
    }

    return new MonthlyResultDto(year, month, String.valueOf(o));
  }

  public YearResultDto getYearlyRentalCount(int year){
    Object o = redisTemplate.opsForHash().get(YEARLY_RENTED_COUNT, String.valueOf(year));

    if (o == null){
      throw new NoSuchElementExistsException(ErrorCode.DATA_NOT_EXISTS);
    }

    List<MonthlyResultDto> result = new ArrayList<>();

    for (int i = 1; i <= 12; i++){
      result.add(getMonthlyRentedCount(year, i));
    }

    return new YearResultDto(year, String.valueOf(o), result);
  }

  public YearResultDto getYearlyReviewCount(int year){
    Object o = redisTemplate.opsForHash().get(YEARLY_REVIEW_COUNT, String.valueOf(year));

    if (o == null){
      throw new NoSuchElementExistsException(ErrorCode.DATA_NOT_EXISTS);
    }

    List<MonthlyResultDto> result = new ArrayList<>();

    for (int i = 1; i <= 12; i++){
      result.add(getMonthlyReviewCount(year, i));
    }

    return new YearResultDto(year, String.valueOf(o), result);
  }

  public YearResultDto getYearlyBookUnavailableCount(int year){
    Object o = redisTemplate.opsForHash().get(YEARLY_BOOK_UNAVAILABLE_COUNT, String.valueOf(year));

    if (o == null){
      throw new NoSuchElementExistsException(ErrorCode.DATA_NOT_EXISTS);
    }

    List<MonthlyResultDto> result = new ArrayList<>();

    for (int i = 1; i <= 12; i++){
      result.add(getMonthlyBookUnavailableCount(year, i));
    }

    return new YearResultDto(year, String.valueOf(o), result);
  }
}
