package com.management.library.domain.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.stream.Stream;

public enum RequestStatus {
  STAND_BY, ACCEPTED, REFUSED;

  // DTO 에서 값을 받을 때 대소문자 구분 없이 사용 가능하도록 역직렬화 후 삽입
  @JsonCreator
  public static RequestStatus parsing(String inputValue) {

    return Stream.of(RequestStatus.values())
        .filter(requestStatus -> requestStatus.toString().equals(inputValue.toUpperCase()))
        .findFirst()
        .orElse(null);
  }
}
