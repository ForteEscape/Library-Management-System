package com.management.library.repository.member;

import com.management.library.domain.member.Member;
import com.management.library.domain.type.MemberRentalStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findByMemberCode(String memberCode);

  @Override
  Page<Member> findAll(Pageable pageable);

  Page<Member> findAllByMemberRentalStatus(MemberRentalStatus memberRentalStatus,
      Pageable pageable);
}
