package com.management.library.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequestAllDto<T> {

  private T content;
  private PageInfo pageInfo;

  public MemberRequestAllDto(T content, PageInfo pageInfo) {
    this.content = content;
    this.pageInfo = pageInfo;
  }
}
