package com.management.library.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginFailedException extends RuntimeException {

  private ErrorCode errorCode;
  private String description;

  public LoginFailedException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.description = errorCode.getDescription();
  }
}
