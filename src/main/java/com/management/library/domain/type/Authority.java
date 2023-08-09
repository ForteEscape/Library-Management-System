package com.management.library.domain.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Authority {
  ROLE_MEMBER("ROLE_MEMBER"),
  ROLE_ADMIN("ROLE_ADMIN");

  private final String role;
}
