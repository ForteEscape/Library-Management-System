package com.management.library.controller.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArrayResponseWrapper<T> {

  @ApiModelProperty(example = "1")
  private Long count;
  private T data;
}
