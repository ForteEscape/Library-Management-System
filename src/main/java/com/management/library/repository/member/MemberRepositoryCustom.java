package com.management.library.repository.member;

import com.management.library.controller.admin.dto.MemberSearchCond;
import com.management.library.domain.member.Member;
import com.management.library.service.member.dto.MemberServiceReadDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {

  Optional<Member> findByMemberNameAndAddress(String name, String legion, String city,
      String street);

  Page<MemberServiceReadDto> findAll(MemberSearchCond cond, Pageable pageable);
}
