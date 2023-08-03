package com.management.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageInfo {

  private int currentPage;
  private int pageSize;
  private int totalElement;
  private int totalPages;

  public PageInfo(int currentPage, int pageSize, int totalElement, int totalPages) {
    this.currentPage = currentPage;
    this.pageSize = pageSize;
    this.totalElement = totalElement;
    this.totalPages = totalPages;
  }
}
