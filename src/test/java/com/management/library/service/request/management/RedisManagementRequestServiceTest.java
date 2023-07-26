package com.management.library.service.request.management;

import static com.management.library.exception.ErrorCode.MANAGEMENT_REQUEST_COUNT_EXCEEDED;
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
class RedisManagementRequestServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Autowired
  private RedisManagementRequestService redisManagementRequestService;
  private static final String KEY = "management-request-count:";

  @AfterEach
  void tearDown(){
    redisManagementRequestService.deleteCache();
  }

  @DisplayName("memberCode 로 등록한 캐시 값을 가져올 수 있다.")
  @Test
  public void getRedisHashValue() throws Exception {
    // given
    // when
    redisManagementRequestService.checkRequestCount("1000001");
    String result = (String) redisTemplate.opsForHash().get(KEY, "1000001");

    // then
    assertThat(result).isEqualTo("4");
  }

  @DisplayName("저장된 여러 개의 memberCode 에 대해 memberCode 로 캐시 값을 가져올 수 있다.")
  @Test
  public void getRedisHashValueWithMultiple() throws Exception {
    // given
    // when
    redisManagementRequestService.checkRequestCount("1000001");
    redisManagementRequestService.checkRequestCount("1000002");
    redisManagementRequestService.checkRequestCount("1000003");

    Map<Object, Object> entries = redisTemplate.opsForHash().entries(KEY);

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
  public void getRedisHashValueWithExceedLimit() throws Exception {
    // given
    // when
    for (int i = 0; i < 5; i++){
      redisManagementRequestService.checkRequestCount("1000001");
    }

    // then
    assertThatThrownBy(() -> redisManagementRequestService.checkRequestCount("1000001"))
        .isInstanceOf(RequestLimitExceededException.class)
        .extracting("errorCode", "description")
        .contains(
            MANAGEMENT_REQUEST_COUNT_EXCEEDED, MANAGEMENT_REQUEST_COUNT_EXCEEDED.getDescription()
        );
  }
}