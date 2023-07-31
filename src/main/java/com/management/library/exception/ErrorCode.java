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
  MEMBER_ALREADY_EXISTS("이미 존재하는 회원입니다."),
  ADMIN_ALREADY_EXISTS("이미 존재하는 관리자 이메일입니다."),
  MANAGEMENT_REQUEST_COUNT_EXCEEDED("운영 개선 요청 등록은 1달에 최대 5회까지 가능합니다."),
  NEW_BOOK_REQUEST_COUNT_EXCEEDED("신간 요청 등록은 1달에 최대 5회까지 가능합니다."),
  REQUEST_NOT_EXISTS("해당 요청이 존재하지 않습니다."),
  REPLY_ALREADY_EXISTS("해당 요청의 답변이 이미 등록되어 있습니다."),
  UNABLE_TO_BOOK_RENTAL("현재 도서 대여가 불가능합니다."),
  BOOK_RENTAL_COUNT_EXCEED("도서는 최대 2개까지 대여가 가능합니다."),
  RENTAL_NOT_EXISTS("해당 대여 기록이 존재하지 않습니다,"),
  MEMBER_STATUS_NOT_AVAILABLE("대여를 할 수 없는 상태에서 연장을 할 수 없습니다."),
  RENTAL_STATUS_NOT_AVAILABLE("해당 대여는 연체되었거나 이미 반납된 상태입니다."),
  RENTAL_ALREADY_EXTEND("해당 대여는 이미 연장된 상태입니다."),
  ADMIN_NOT_EXISTS("해당 이메일로 가입된 관리자가 없습니다."),
  PASSWORD_NOT_MATCH("비밀번호가 틀립니다"),
  OVERDUE_RENTAL_EXISTS("연체된 대여가 존재합니다.");

  private final String description;
}
