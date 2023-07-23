package com.management.library.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  BOOK_NOT_EXISTS("존재하지 않는 책입니다."),
  BOOK_ALREADY_EXISTS("해당 책은 이미 도서관에 비치되어 있습니다."),
  INVALID_RANGE("잘못된 분류 번호 범위입니다.");

  private final String description;
}
