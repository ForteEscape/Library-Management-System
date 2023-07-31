package com.management.library.service.request;

import static com.management.library.exception.ErrorCode.MANAGEMENT_REQUEST_COUNT_EXCEEDED;
import static com.management.library.exception.ErrorCode.NEW_BOOK_REQUEST_COUNT_EXCEEDED;
import static com.management.library.exception.ErrorCode.REPLY_ALREADY_EXISTS;

import com.management.library.exception.InvalidAccessException;
import com.management.library.exception.RequestLimitExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisRequestService {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String INIT_REQUEST_COUNT = "5";
  private static final String MANAGEMENT_CACHE_KEY = "management-request-count:";
  private static final String NEW_BOOK_CACHE_KEY = "book-request-count:";
  private static final String NEW_BOOK_REQUEST_PREFIX = "book-request-id:";
  private static final String MANAGEMENT_REQUEST_PREFIX = "management-request-id:";

  /**
   * redis cache 로 해당 memberCode 의 request count 가 남아있는지 확인 만약 존재하지 않는다면 memberCode 의 request count
   * 는 5로 초기화 만약 memberCode 의 request count = 0이라면 더 이상 진행 불가.
   */
  public void checkManagementRequestCount(String memberCode) {
    HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
    hash.putIfAbsent(MANAGEMENT_CACHE_KEY, memberCode, INIT_REQUEST_COUNT);

    int count = Integer.parseInt((String) hash.get(MANAGEMENT_CACHE_KEY, memberCode));
    if (count <= 0) {
      throw new RequestLimitExceededException(MANAGEMENT_REQUEST_COUNT_EXCEEDED);
    }

    hash.increment(MANAGEMENT_CACHE_KEY, memberCode, -1);
  }

  public void checkNewBookRequestCount(String memberCode) {
    HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
    hash.putIfAbsent(NEW_BOOK_CACHE_KEY, memberCode, INIT_REQUEST_COUNT);

    int count = Integer.parseInt((String) hash.get(NEW_BOOK_CACHE_KEY, memberCode));
    if (count <= 0) {
      throw new RequestLimitExceededException(NEW_BOOK_REQUEST_COUNT_EXCEEDED);
    }

    hash.increment(NEW_BOOK_CACHE_KEY, memberCode, -1);
  }

  /**
   * 각 요청에 대한 캐시를 사용하여 하나의 요청에 하나의 답변만 등록될 수 있도록 제약을 둘 수 있다. 요청 게시글 등록 시 해당 게시글의 id를 사용하여 캐시에 등록한다.
   * 답변을 등록할 때 redis 에서 해당 게시글의 id 를 키로 가지는 캐시가 존재하는지를 확인한다. 만약 존재하지 않으면 이미 답변이 등록되었다는 것으로 간주하고 답변이
   * 등록될 수 없도록 한다. 답변 등록 시 게시글 id를 키로 가지는 캐시를 redis 에서 제거한다. 제거의 경우 atomic 하게 동작하도록 redis template 를
   * 통해 구현 가능하므로 동시성 문제도 같이 해결할 수 있다.
   */
  public void addBookRequestCache(Long bookRequestId) {
    redisTemplate.opsForValue().set(NEW_BOOK_REQUEST_PREFIX + bookRequestId, "");
  }

  public void addManagementRequestCache(Long managementRequestId) {
    redisTemplate.opsForValue().set(MANAGEMENT_REQUEST_PREFIX + managementRequestId, "");
  }

  public void removeBookRequestCache(Long bookRequestId) {
    String result = redisTemplate.opsForValue()
        .getAndDelete(NEW_BOOK_REQUEST_PREFIX + bookRequestId);

    if (result == null) {
      throw new InvalidAccessException(REPLY_ALREADY_EXISTS);
    }
  }

  public void removeManagementRequestCache(Long managementRequestId) {
    String result = redisTemplate.opsForValue()
        .getAndDelete(MANAGEMENT_REQUEST_PREFIX + managementRequestId);

    if (result == null) {
      throw new InvalidAccessException(REPLY_ALREADY_EXISTS);
    }
  }

  // 남은 운영 개선 요청 가능 횟수
  public String getManagementRequestCount(String memberCode) {
    Object managementData = redisTemplate.opsForHash().get(MANAGEMENT_CACHE_KEY, memberCode);

    if (managementData == null){
      return INIT_REQUEST_COUNT;
    }

    return String.valueOf(managementData);
  }

  // 남은 운영 개선 요청 가능 횟수
  public String getNewBookRequestCount(String memberCode) {
    Object newBookData = redisTemplate.opsForHash().get(NEW_BOOK_CACHE_KEY, memberCode);

    if (newBookData == null){
      return INIT_REQUEST_COUNT;
    }

    return String.valueOf(newBookData);
  }

  public void deleteCache(String key) {
    redisTemplate.delete(key);
  }
}
