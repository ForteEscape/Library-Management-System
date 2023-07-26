package com.management.library.repository.member;

import com.management.library.domain.member.Member;
import java.util.Optional;

public interface MemberRepositoryCustom {

  Optional<Member> findByMemberNameAndAddress(String name, String legion, String city,
      String street);

  Optional<String> findTopByOrderByIdDesc();
}
