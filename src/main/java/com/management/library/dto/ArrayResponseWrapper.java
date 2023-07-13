package com.management.library.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ArrayResponseWrapper<T> {

  private int count;
  private T data;
}
