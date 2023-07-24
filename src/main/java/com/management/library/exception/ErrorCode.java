package com.management.library.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
  BOOK_NOT_EXISTS("존재하지 않는 책입니다."),
  BOOK_ALREADY_EXISTS("해당 책은 이미 도서관에 비치되어 있습니다."),
  INVALID_RANGE("잘못된 분류 번호 범위입니다."),
  ALREADY_IN_USE("잠시 후 다시 시도하세요"),
  UNEXPECTED_ERROR("예상하지 못한 오류입니다. 잠시 후 다시 시도해주세요"),
  MEMBER_NOT_EXISTS("존재하지 않는 회원입니다."),
  DUPLICATE_MEMBER_CODE("동일한 멤버 코드가 존재합니다. 다시 시도해 주세요"),
  MEMBER_ALREADY_EXISTS("이미 존재하는 회원입니다.");

  private final String description;
}
