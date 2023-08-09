package com.management.library.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageInfo {

  @ApiModelProperty(example = "0")
  private int currentPage;
  @ApiModelProperty(example = "20")
  private int pageSize;
  @ApiModelProperty(example = "100")
  private int totalElement;
  @ApiModelProperty(example = "5")
  private int totalPages;

  public PageInfo(int currentPage, int pageSize, int totalElement, int totalPages) {
    this.currentPage = currentPage;
    this.pageSize = pageSize;
    this.totalElement = totalElement;
    this.totalPages = totalPages;
  }
}
