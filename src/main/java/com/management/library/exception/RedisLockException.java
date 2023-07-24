package com.management.library.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RedisLockException extends RuntimeException{

  private ErrorCode errorCode;
  private String description;

  public RedisLockException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.description = errorCode.getDescription();
  }
}
