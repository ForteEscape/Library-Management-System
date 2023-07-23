package com.management.library.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DuplicateException extends RuntimeException{

  private ErrorCode errorCode;
  private String description;

  public DuplicateException(ErrorCode errorCode) {
    this.errorCode = errorCode;
    this.description = errorCode.getDescription();
  }
}
