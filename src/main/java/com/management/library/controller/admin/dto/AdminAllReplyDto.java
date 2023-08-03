package com.management.library.controller.admin.dto;

import com.management.library.dto.PageInfo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminAllReplyDto<T> {

  private T data;
  private PageInfo pageInfo;

  public AdminAllReplyDto(T data, PageInfo pageInfo) {
    this.data = data;
    this.pageInfo = pageInfo;
  }
}
