package com.management.library.service.book.recommend;

import static com.management.library.service.book.recommend.dto.BookRecommendResponseDto.ReviewRate;

import com.management.library.service.book.recommend.dto.BookRecommendResponseDto.RentedCount;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookRecommendService {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String BOOK_RENTED_COUNT = "book-rented-count";
  private static final String BOOK_REVIEW_RATE = "book-review-rate";

  public List<RentedCount> getRecommendBookListByRentalCount() {
    Set<TypedTuple<String>> typedTuples = redisTemplate.opsForZSet()
        .reverseRangeWithScores(BOOK_RENTED_COUNT, 0, 9);

    if (typedTuples == null) {
      return new ArrayList<>();
    }

    return typedTuples.stream()
        .map(RentedCount::of)
        .collect(Collectors.toList());
  }

  public List<ReviewRate> getRecommendBookListByReviewRate() {
    Set<TypedTuple<String>> typedTuples = redisTemplate.opsForZSet()
        .reverseRangeWithScores(BOOK_REVIEW_RATE, 0, 9);

    if (typedTuples == null) {
      return new ArrayList<>();
    }

    return typedTuples.stream()
        .map(ReviewRate::of)
        .collect(Collectors.toList());
  }

}
