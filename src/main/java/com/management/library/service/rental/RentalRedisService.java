package com.management.library.service.rental;

import static com.management.library.exception.ErrorCode.BOOK_RENTAL_COUNT_EXCEED;

import com.management.library.exception.ErrorCode;
import com.management.library.exception.InvalidAccessException;
import com.management.library.exception.RentalException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RentalRedisService {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String RENTAL_REDIS_KEY = "rental-count";
  private static final String INIT_AVAILABLE_COUNT = "2";
  private static final String PENALTY_MEMBER_KEY = "penalty:";
  private static final String BOOK_RENTED_COUNT = "book-rented-count";

  public void checkMemberRentalBookCount(String memberCode) {
    HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
    hash.putIfAbsent(RENTAL_REDIS_KEY, memberCode, INIT_AVAILABLE_COUNT);

    String availableCountData = String.valueOf(hash.get(RENTAL_REDIS_KEY, memberCode));
    int availableCount = Integer.parseInt(availableCountData);

    if (availableCount <= 0) {
      throw new RentalException(BOOK_RENTAL_COUNT_EXCEED);
    }

    hash.increment(RENTAL_REDIS_KEY, memberCode, -1);
  }

  public LocalDate addMemberOverdueData(String memberCode, int overdueDays, LocalDate penaltyEndDate) {
    String penaltyData = redisTemplate.opsForValue().get(PENALTY_MEMBER_KEY + memberCode);

    if (StringUtils.hasText(penaltyData)) {
      LocalDate currentPenaltyDate = LocalDate.parse(penaltyData, DateTimeFormatter.ISO_DATE);
      penaltyEndDate = currentPenaltyDate.plusDays(overdueDays);
    }

    Period expireTime = Period.between(LocalDate.now(), penaltyEndDate);

    redisTemplate.opsForValue()
        .set(PENALTY_MEMBER_KEY + memberCode, String.valueOf(penaltyEndDate), expireTime.getDays(),
            TimeUnit.DAYS);

    return penaltyEndDate;
  }

  public void addBookRentedCount(String bookTitle){
    ZSetOperations<String, String> sortedSet = redisTemplate.opsForZSet();

    Boolean isKeyExists = sortedSet.addIfAbsent(BOOK_RENTED_COUNT, bookTitle, 1);

    if (isKeyExists == null){
      throw new InvalidAccessException(ErrorCode.UNEXPECTED_ERROR);
    }

    if (!isKeyExists){
      sortedSet.incrementScore(BOOK_RENTED_COUNT, bookTitle, 1);
    }
  }

  public String getMemberRemainRentalCount(String memberCode){
    Object data = redisTemplate.opsForHash().get(RENTAL_REDIS_KEY, memberCode);

    if (data == null){
      return INIT_AVAILABLE_COUNT;
    }

    return String.valueOf(data);
  }

  public boolean checkMemberRentalPenalty(String memberCode) {
    String result = redisTemplate.opsForValue().get(PENALTY_MEMBER_KEY + memberCode);

    return StringUtils.hasText(result);
  }

  public void addMemberRentalBookCount(String memberCode) {
    redisTemplate.opsForHash()
        .increment(RENTAL_REDIS_KEY, memberCode, 1);
  }
}
