package com.management.library.repository.member;

import com.management.library.domain.member.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {

  Optional<Member> findByMemberCode(String memberCode);
}
