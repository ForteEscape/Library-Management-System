package com.management.library.service.review;

import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.AbstractContainerBaseTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class BookReviewRedisServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Autowired
  private BookReviewRedisService bookReviewRedisService;

  private static final String REVIEW_CACHE_PREFIX = "review-member:";
  private static final String BOOK_REVIEW_RATE = "book-review-rate";
  private static final String BOOK_REVIEW_COUNT = "book-review-count";

  @AfterEach
  void tearDown(){
    redisTemplate.delete(BOOK_REVIEW_RATE);
    redisTemplate.delete(BOOK_REVIEW_COUNT);
    redisTemplate.delete(REVIEW_CACHE_PREFIX + "1000001");
  }

  @DisplayName("회원이 리뷰한 어떤 도서에 대해 캐싱을 할 수 있다.")
  @Test
  public void addReviewCache() throws Exception {
    // given
    String memberCode = "1000001";
    String bookName = "book";

    // when
    bookReviewRedisService.addReviewCache(memberCode, bookName);

    // then
    String result = String.valueOf(
        redisTemplate.opsForHash().get(REVIEW_CACHE_PREFIX + memberCode, bookName));

    assertThat(result).isNotNull();
    assertThat(result).isEqualTo("1");
  }

  @DisplayName("회원이 특정 도서에 리뷰를 남겼는지 확인할 수 있다.")
  @Test
  public void checkReviewCache() throws Exception {
    // given
    String memberCode = "1000001";
    String bookName = "book";

    bookReviewRedisService.addReviewCache(memberCode, bookName);

    // when
    // then
    assertThat(bookReviewRedisService.getReviewCache(memberCode, bookName)).isTrue();
  }

  @DisplayName("도서에 대한 평점을 계산하여 저장할 수 있다.")
  @Test
  public void addReviewRate() throws Exception {
    // given
    String bookTitle = "book1";
    int rate = 5;

    // when
    bookReviewRedisService.addReviewRate(bookTitle, rate);

    // then
    String count = String.valueOf(redisTemplate.opsForHash().get(BOOK_REVIEW_COUNT, bookTitle));
    Double score = redisTemplate.opsForZSet().score(BOOK_REVIEW_RATE, bookTitle);

    assertThat(count).isNotNull()
        .isEqualTo("1");
    assertThat(score).isEqualTo(5.0);
  }

  @DisplayName("도서에 대한 여러 개의 평점을 계산하여 저장할 수 있다.")
  @Test
  public void addReviewRateWithMultipleRate() throws Exception {
    // given
    String bookTitle = "book1";
    int rate1 = 5;
    int rate2 = 4;
    int rate3 = 2;

    // when
    bookReviewRedisService.addReviewRate(bookTitle, rate1);
    bookReviewRedisService.addReviewRate(bookTitle, rate2);
    bookReviewRedisService.addReviewRate(bookTitle, rate3);

    // then
    String count = String.valueOf(redisTemplate.opsForHash().get(BOOK_REVIEW_COUNT, bookTitle));
    Double score = redisTemplate.opsForZSet().score(BOOK_REVIEW_RATE, bookTitle);

    assertThat(count).isNotNull()
        .isEqualTo("3");
    assertThat(score).isEqualTo(3.67);
  }
}