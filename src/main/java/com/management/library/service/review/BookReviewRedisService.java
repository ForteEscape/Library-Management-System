package com.management.library.service.review;

import com.management.library.exception.ErrorCode;
import com.management.library.exception.InvalidAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookReviewRedisService {

  private final RedisTemplate<String, String> redisTemplate;

  private static final String REVIEW_CACHE_PREFIX = "review-member:";
  private static final String BOOK_REVIEW_RATE = "book-review-rate";
  private static final String BOOK_REVIEW_COUNT = "book-review-count";

  public void addReviewCache(String memberCode, String bookName) {
    redisTemplate.opsForHash().put(REVIEW_CACHE_PREFIX + memberCode, bookName, "1");
  }

  public void addReviewRate(String bookName, int rate){
    redisTemplate.opsForHash().putIfAbsent(BOOK_REVIEW_COUNT, bookName, "0");
    redisTemplate.opsForZSet().addIfAbsent(BOOK_REVIEW_RATE, bookName, 0);

    Double score = redisTemplate.opsForZSet().score(BOOK_REVIEW_RATE, bookName);

    if (score == null){
      throw new InvalidAccessException(ErrorCode.UNEXPECTED_ERROR);
    }

    redisTemplate.opsForHash().increment(BOOK_REVIEW_COUNT, bookName, 1);
    String cntStr = String.valueOf(redisTemplate.opsForHash().get(BOOK_REVIEW_COUNT, bookName));

    double cnt = Double.parseDouble(cntStr);
    long round = 0L;

    if (cntStr.equals("1")){
      round = Math.round(((score + rate) / cnt) * 100);
    } else{
      round = Math.round(((score * (cnt - 1) + rate) / cnt) * 100);
    }
    score = round / 100.0;

    redisTemplate.opsForZSet().add(BOOK_REVIEW_RATE, bookName, score);
  }

  public boolean getReviewCache(String memberCode, String bookName){
    return redisTemplate.opsForHash().get(REVIEW_CACHE_PREFIX + memberCode, bookName) != null;
  }
}
