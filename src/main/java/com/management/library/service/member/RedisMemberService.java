package com.management.library.service.member;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisMemberService {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String INIT_MEMBER_CODE = "100000000";

  public Long getMemberCode(){
    redisTemplate.opsForValue().setIfAbsent("memberCode", INIT_MEMBER_CODE);

    return redisTemplate.opsForValue().increment("memberCode", 1);
  }
}
