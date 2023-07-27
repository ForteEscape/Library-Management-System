package com.management.library.service.request;

import static com.management.library.exception.ErrorCode.MANAGEMENT_REQUEST_COUNT_EXCEEDED;
import static com.management.library.exception.ErrorCode.NEW_BOOK_REQUEST_COUNT_EXCEEDED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.entry;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.exception.RequestLimitExceededException;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
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
  @AfterEach
  void tearDown(){
    redisManagementRequestService.deleteCache(MANAGEMENT_CACHE_KEY);
    redisManagementRequestService.deleteCache(NEW_BOOK_CACHE_KEY);
  }

  @DisplayName("memberCode 로 등록한 운영 요청 횟수 캐시 값을 가져올 수 있다.")
  @Test
  public void getManagementRequestRedisHashValue() throws Exception {
    // given
    // when
    redisManagementRequestService.checkManagementRequestCount("1000001");
    String result = (String) redisTemplate.opsForHash().get(MANAGEMENT_CACHE_KEY, "1000001");

    // then
    assertThat(result).isEqualTo("4");
  }

  @DisplayName("저장된 여러 개의 memberCode 에 대해 memberCode 로 운영 요청 횟수 캐시 값을 가져올 수 있다.")
  @Test
  public void getManagementRequestRedisHashValueWithMultiple() throws Exception {
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
  public void getManageRequestRedisHashValueWithExceedLimit() throws Exception {
    // given
    // when
    for (int i = 0; i < 5; i++){
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
  public void getNewBookRedisHashValue() throws Exception {
    // given
    // when
    redisManagementRequestService.checkNewBookRequestCount("1000001");
    String result = (String) redisTemplate.opsForHash().get(NEW_BOOK_CACHE_KEY, "1000001");

    // then
    assertThat(result).isEqualTo("4");
  }

  @DisplayName("저장된 여러 개의 memberCode 에 대해 memberCode 로 신간 도서 요청 횟수 캐시 값을 가져올 수 있다.")
  @Test
  public void getNewBookRedisHashValueWithMultiple() throws Exception {
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
  public void getNewBookRequestRedisHashValueWithExceedLimit() throws Exception {
    // given
    // when
    for (int i = 0; i < 5; i++){
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
}