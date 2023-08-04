package com.management.library.service.scheduled;

import com.management.library.repository.book.BookRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.repository.review.BookReviewRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ScheduledService {

  private final RedisTemplate<String, String> redisTemplate;

  private final BookRentalRepository bookRentalRepository;
  private final BookReviewRepository bookReviewRepository;
  private final BookRepository bookRepository;

  private static final String MANAGEMENT_CACHE_KEY = "management-request-count:";
  private static final String NEW_BOOK_CACHE_KEY = "book-request-count:";
  private static final String BOOK_RENTED_COUNT = "book-rented-count";
  private static final String MONTHLY_RENTED_COUNT = "monthly-rented-count";
  private static final String MONTHLY_REVIEW_COUNT = "monthly-review-count";
  private static final String MONTHLY_BOOK_UNAVAILABLE_COUNT = "monthly-book-unavailable-count";
  private static final String YEARLY_RENTED_COUNT = "yearly-rented-count";
  private static final String YEARLY_REVIEW_COUNT = "yearly-review-count";
  private static final String YEARLY_BOOK_UNAVAILABLE_COUNT = "yearly-book-unavailable-count";

  // 캐시 초기화 - 매 달 요청 횟수
  @Scheduled(cron = "0 5 0 1 * *", zone = "Asia/Seoul")
  public void initRequestCount() {
    redisTemplate.delete(MANAGEMENT_CACHE_KEY);
    redisTemplate.delete(NEW_BOOK_CACHE_KEY);
  }

  // 랭킹 초기화 - 매년 1월 1일 00시 05분 수행
  @Scheduled(cron = "0 5 0 1 1 *", zone = "Asia/Seoul")
  public void initRentCountRanking() {
    redisTemplate.delete(BOOK_RENTED_COUNT);
  }

  // 월간 도서 대여 수 정산
  @Scheduled(cron = "0 10 0 1 * *", zone = "Asia/Seoul")
  public void monthlyRentalSettle() {
    LocalDate startDate = YearMonth.now().minusMonths(2).atEndOfMonth();
    LocalDate endDate = YearMonth.now().atDay(1);

    int monthValue = endDate.getMonthValue() - 1;
    int year = startDate.getYear();

    if (monthValue == 0) {
      monthValue = 12;
    }

    String hashKey = year + "-" + monthValue;

    Long resultCount = bookRentalRepository.countByRentalByDate(startDate, endDate);

    redisTemplate.opsForHash().put(MONTHLY_RENTED_COUNT, hashKey, resultCount.toString());
  }

  // 월 간 리뷰 수 정산
  @Scheduled(cron = "0 10 0 1 * *", zone = "Asia/Seoul")
  public void monthlyReviewSettle() {
    LocalDate startDate = YearMonth.now().minusMonths(2).atEndOfMonth();
    LocalDate endDate = YearMonth.now().atDay(1);

    int monthValue = endDate.getMonthValue() - 1;
    int year = startDate.getYear();

    if (monthValue == 0) {
      monthValue = 12;
    }
    String hashKey = year + "-" + monthValue;

    Long resultCount = bookReviewRepository.countByReviewDate(startDate, endDate);

    redisTemplate.opsForHash().put(MONTHLY_REVIEW_COUNT, hashKey, resultCount.toString());
  }

  // 월 간 도서 손/망실 정산
  @Scheduled(cron = "0 10 0 1 * *", zone = "Asia/Seoul")
  public void monthlyUnavailableBookSettle() {
    int monthValue = LocalDate.now().getMonthValue() - 1;
    int year = LocalDate.now().getYear();

    if (monthValue == 0) {
      monthValue = 12;
      year -= 1;
    }
    String hashKey = year + "-" + monthValue;

    Long resultCount = bookRepository.countByBookUnavailableStatus();

    redisTemplate.opsForHash().put(MONTHLY_BOOK_UNAVAILABLE_COUNT, hashKey, resultCount.toString());
  }

  // 연간 도서 대여 정산
  @Scheduled(cron = "0 15 0 1 1 *", zone = "Asia/Seoul")
  public void yearlyRentalSettle() {
    long resultCount = 0L;
    int year = LocalDate.now().getYear() - 1;

    for (int i = 1; i <= 12; i++) {
      String monthlyCount = (String) redisTemplate.opsForHash()
          .get(MONTHLY_RENTED_COUNT, year + "-" + i);

      if (monthlyCount == null) {
        log.error("yearly rental settle error occurred : month = {}", i);
        continue;
      }

      resultCount += Integer.parseInt(monthlyCount);
    }

    redisTemplate.opsForHash().put(YEARLY_RENTED_COUNT, String.valueOf(year),
        String.valueOf(resultCount));
  }

  // 연간 도서 리뷰 수 정산
  @Scheduled(cron = "0 15 0 1 1 *", zone = "Asia/Seoul")
  public void yearlyReviewSettle() {
    long resultCount = 0L;
    int year = LocalDate.now().getYear() - 1;

    for (int i = 1; i <= 12; i++) {
      String monthlyCount = (String) redisTemplate.opsForHash()
          .get(MONTHLY_REVIEW_COUNT, year + "-" + i);

      if (monthlyCount == null) {
        log.error("yearly review settle error occurred : month = {}", i);
        continue;
      }

      resultCount += Integer.parseInt(monthlyCount);
    }

    redisTemplate.opsForHash().put(YEARLY_REVIEW_COUNT, String.valueOf(year),
        String.valueOf(resultCount));
  }

  // 연간 도서 손/망실 정산
  @Scheduled(cron = "0 15 0 1 1 *", zone = "Asia/Seoul")
  public void yearlyBookUnavailableSettle() {
    long resultCount = 0L;
    int year = LocalDate.now().getYear() - 1;

    for (int i = 1; i <= 12; i++) {
      String monthlyCount = (String) redisTemplate.opsForHash()
          .get(MONTHLY_BOOK_UNAVAILABLE_COUNT, year + "-" + i);

      if (monthlyCount == null) {
        log.error("yearly book settle error occurred : month = {}", i);
        continue;
      }

      resultCount += Integer.parseInt(monthlyCount);
    }

    redisTemplate.opsForHash()
        .put(YEARLY_BOOK_UNAVAILABLE_COUNT, String.valueOf(year),
            String.valueOf(resultCount));
  }
}
