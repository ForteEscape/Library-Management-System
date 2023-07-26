package com.management.library.service.request.management;

import static com.management.library.exception.ErrorCode.MANAGEMENT_REQUEST_COUNT_EXCEEDED;

import com.management.library.exception.RequestLimitExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisManagementRequestService {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String INIT_REQUEST_COUNT = "5";
  private static final String KEY = "management-request-count:";

  // redis cache 로 해당 memberCode 의 request count 가 남아있는지 확인
  // 만약 존재하지 않는다면 memberCode 의 request count 는 5로 초기화
  // 만약 memberCode 의 request count = 0이라면 더 이상 진행 불가.

  // redis hash로 하면 어떨까? key: management-request-count, hash-key: memberCode, value: count
  public void checkRequestCount(String memberCode){
    HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
    hash.putIfAbsent(KEY, memberCode, INIT_REQUEST_COUNT);

    int count = Integer.parseInt((String) hash.get(KEY, memberCode));
    if (count <= 0){
      throw new RequestLimitExceededException(MANAGEMENT_REQUEST_COUNT_EXCEEDED);
    }

    hash.increment(KEY, memberCode, -1);
  }

  public void deleteCache(){
    redisTemplate.delete(KEY);
  }
}
