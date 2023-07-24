package com.management.library.service.member.generator;

import com.management.library.service.Generator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberCodeGenerator implements Generator<String> {

  @Override
  public String generate(String value) {
    long nextMemberCodeLong = Long.parseLong(value);

    return String.valueOf(nextMemberCodeLong + 1L);
  }
}
