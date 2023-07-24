package com.management.library.service.member.generator;

import com.management.library.service.Generator;
import org.springframework.stereotype.Component;

@Component
public class MemberPasswordGenerator implements Generator<String> {

  private static final String SUFFIX = "!@#";

  @Override
  public String generate(String value) {
    return value + SUFFIX;
  }
}
