package com.management.library.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArrayResponseWrapper<T> {

  private Long count;
  private T data;
}
