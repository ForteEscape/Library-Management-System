package com.management.library.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewAllDto<T> {

  private T content;
  private PageInfo pageInfo;

  public ReviewAllDto(T content, PageInfo pageInfo) {
    this.content = content;
    this.pageInfo = pageInfo;
  }
}
