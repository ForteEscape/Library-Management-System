package com.management.library.service.request;

import static com.management.library.exception.ErrorCode.MANAGEMENT_REQUEST_COUNT_EXCEEDED;
import static com.management.library.exception.ErrorCode.NEW_BOOK_REQUEST_COUNT_EXCEEDED;
import static com.management.library.exception.ErrorCode.REPLY_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.exception.InvalidAccessException;
import com.management.library.exception.RequestLimitExceededException;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

@SpringBootTest
class RedisRequestServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Autowired
  private RedisRequestService redisManagementRequestService;
  private static final String MANAGEMENT_CACHE_KEY = "management-request-count:";
  private static final String NEW_BOOK_CACHE_KEY = "book-request-count:";
  private static final String NEW_BOOK_REQUEST_PREFIX = "book-request-id:";
  private static final String MANAGEMENT_REQUEST_PREFIX = "management-request-id:";

  @AfterEach
  void tearDown() {
    redisManagementRequestService.deleteCache(MANAGEMENT_CACHE_KEY);
    redisManagementRequestService.deleteCache(NEW_BOOK_CACHE_KEY);
    redisManagementRequestService.deleteCache(NEW_BOOK_REQUEST_PREFIX + 1L);
    redisManagementRequestService.deleteCache(MANAGEMENT_REQUEST_PREFIX + 1L);
  }

  @DisplayName("memberCode 로 등록한 운영 요청 횟수 캐시 값을 가져올 수 있다.")
  @Test
  public void getManagementRequestRedisHashValue() {
    // given
    // when
    redisManagementRequestService.checkManagementRequestCount("1000001");
    String result = (String) redisTemplate.opsForHash().get(MANAGEMENT_CACHE_KEY, "1000001");

    // then
    assertThat(result).isEqualTo("4");
  }

  @DisplayName("저장된 여러 개의 memberCode 에 대해 memberCode 로 운영 요청 횟수 캐시 값을 가져올 수 있다.")
  @Test
  public void getManagementRequestRedisHashValueWithMultiple() {
    // given
    // when
    redisManagementRequestService.checkManagementRequestCount("1000001");
    redisManagementRequestService.checkManagementRequestCount("1000002");
    redisManagementRequestService.checkManagementRequestCount("1000003");

    Map<Object, Object> entries = redisTemplate.opsForHash().entries(MANAGEMENT_CACHE_KEY);

    // then
    assertThat(entries).hasSize(3);
    assertThat(entries).contains(
        entry("1000001", "4"),
        entry("1000002", "4"),
        entry("1000003", "4")
    );
  }

  @DisplayName("하나의 key에 대해 5번 카운트를 내린 상태에서 다시 카운트를 내리려고 하면 예외가 발생한다.")
  @Test
  public void getManageRequestRedisHashValueWithExceedLimit() {
    // given
    // when
    for (int i = 0; i < 5; i++) {
      redisManagementRequestService.checkManagementRequestCount("1000001");
    }

    // then
    assertThatThrownBy(() -> redisManagementRequestService.checkManagementRequestCount("1000001"))
        .isInstanceOf(RequestLimitExceededException.class)
        .extracting("errorCode", "description")
        .contains(
            MANAGEMENT_REQUEST_COUNT_EXCEEDED, MANAGEMENT_REQUEST_COUNT_EXCEEDED.getDescription()
        );
  }

  @DisplayName("memberCode 로 등록한 신간 도서 요청 횟수 캐시 값을 가져올 수 있다.")
  @Test
  public void getNewBookRedisHashValue() {
    // given
    // when
    redisManagementRequestService.checkNewBookRequestCount("1000001");
    String result = (String) redisTemplate.opsForHash().get(NEW_BOOK_CACHE_KEY, "1000001");

    // then
    assertThat(result).isEqualTo("4");
  }

  @DisplayName("저장된 여러 개의 memberCode 에 대해 memberCode 로 신간 도서 요청 횟수 캐시 값을 가져올 수 있다.")
  @Test
  public void getNewBookRedisHashValueWithMultiple() {
    // given
    // when
    redisManagementRequestService.checkNewBookRequestCount("1000001");
    redisManagementRequestService.checkNewBookRequestCount("1000002");
    redisManagementRequestService.checkNewBookRequestCount("1000003");

    Map<Object, Object> entries = redisTemplate.opsForHash().entries(NEW_BOOK_CACHE_KEY);

    // then
    assertThat(entries).hasSize(3);
    assertThat(entries).contains(
        entry("1000001", "4"),
        entry("1000002", "4"),
        entry("1000003", "4")
    );
  }

  @DisplayName("하나의 key에 대해 5번 카운트를 내린 상태에서 다시 카운트를 내리려고 하면 예외가 발생한다.")
  @Test
  public void getNewBookRequestRedisHashValueWithExceedLimit() {
    // given
    // when
    for (int i = 0; i < 5; i++) {
      redisManagementRequestService.checkNewBookRequestCount("1000001");
    }

    // then
    assertThatThrownBy(() -> redisManagementRequestService.checkNewBookRequestCount("1000001"))
        .isInstanceOf(RequestLimitExceededException.class)
        .extracting("errorCode", "description")
        .contains(
            NEW_BOOK_REQUEST_COUNT_EXCEEDED, NEW_BOOK_REQUEST_COUNT_EXCEEDED.getDescription()
        );
  }

  @DisplayName("신간 도서 요청 id로 캐시를 만들 수 있다.")
  @Test
  public void addBookRequestCache() {
    // given
    redisManagementRequestService.addBookRequestCache(1L);

    // when
    String result = redisTemplate.opsForValue().get(NEW_BOOK_REQUEST_PREFIX + 1L);

    // then
    assertThat(result).isEqualTo("");
  }

  @DisplayName("신간 도서 요청 id로 캐시를 제거할 수 있다.")
  @Test
  public void removeBookRequestCache() {
    // given
    redisManagementRequestService.addBookRequestCache(1L);

    // when
    // then
    Assertions.assertDoesNotThrow(() -> redisManagementRequestService.removeBookRequestCache(1L));
  }

  @DisplayName("존재하지 않는 도서 id의 캐시를 제거하려고 하면 예외가 발생한다.")
  @Test
  public void removeBookRequestCacheWithIdNotExists() {
    // given
    redisManagementRequestService.addBookRequestCache(1L);

    // when
    // then
    assertThatThrownBy(() -> redisManagementRequestService.removeBookRequestCache(5L))
        .isInstanceOf(InvalidAccessException.class)
        .extracting("errorCode", "description")
        .contains(
            REPLY_ALREADY_EXISTS, REPLY_ALREADY_EXISTS.getDescription()
        );
  }

  @DisplayName("이미 캐시에서 제거한 도서 id의 캐시를 제거하려고 하면 예외가 발생한다.")
  @Test
  public void removeBookRequestCacheWithAlreadyDeletedId() {
    // given
    redisManagementRequestService.addBookRequestCache(1L);

    // when
    redisManagementRequestService.removeBookRequestCache(1L);

    // then
    assertThatThrownBy(() -> redisManagementRequestService.removeBookRequestCache(1L))
        .isInstanceOf(InvalidAccessException.class)
        .extracting("errorCode", "description")
        .contains(
            REPLY_ALREADY_EXISTS, REPLY_ALREADY_EXISTS.getDescription()
        );
  }

  @DisplayName("운영 개선 요청 id로 캐시를 만들 수 있다.")
  @Test
  public void addManagementRequestCache() {
    // given
    redisManagementRequestService.addManagementRequestCache(1L);

    // when
    String result = redisTemplate.opsForValue().get(MANAGEMENT_REQUEST_PREFIX + 1L);

    // then
    assertThat(result).isEqualTo("");
  }

  @DisplayName("운영 개선 요청 id로 캐시를 제거할 수 있다.")
  @Test
  public void removeManagementRequestCache() {
    // given
    redisManagementRequestService.addManagementRequestCache(1L);

    // when
    // then
    Assertions.assertDoesNotThrow(
        () -> redisManagementRequestService.removeManagementRequestCache(1L));
  }

  @DisplayName("존재하지 않는 운영 개선 요청 id의 캐시를 제거하려고 하면 예외가 발생한다.")
  @Test
  public void removeManagementRequestCacheWithIdNotExists() {
    // given
    redisManagementRequestService.addManagementRequestCache(1L);

    // when
    // then
    assertThatThrownBy(() -> redisManagementRequestService.removeManagementRequestCache(5L))
        .isInstanceOf(InvalidAccessException.class)
        .extracting("errorCode", "description")
        .contains(
            REPLY_ALREADY_EXISTS, REPLY_ALREADY_EXISTS.getDescription()
        );
  }

  @DisplayName("이미 캐시에서 제거한 운영 개선 요청 id의 캐시를 제거하려고 하면 예외가 발생한다.")
  @Test
  public void removeManagementRequestCacheWithAlreadyDeletedId() {
    // given
    redisManagementRequestService.addManagementRequestCache(1L);

    // when
    redisManagementRequestService.removeManagementRequestCache(1L);

    // then
    assertThatThrownBy(() -> redisManagementRequestService.removeManagementRequestCache(1L))
        .isInstanceOf(InvalidAccessException.class)
        .extracting("errorCode", "description")
        .contains(
            REPLY_ALREADY_EXISTS, REPLY_ALREADY_EXISTS.getDescription()
        );
  }
}