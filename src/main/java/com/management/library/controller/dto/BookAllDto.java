package com.management.library.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookAllDto<T> {

  private T content;
  private PageInfo pageInfo;

  public BookAllDto(T content, PageInfo pageInfo) {
    this.content = content;
    this.pageInfo = pageInfo;
  }
}
