package com.management.library.repository.member;

import static com.management.library.domain.member.QMember.*;

import com.management.library.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import javax.persistence.EntityManager;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public MemberRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Optional<Member> findByMemberNameAndAddress(String name, String legion, String city,
      String street) {
    Member result = queryFactory.select(member)
        .from(member)
        .where(
            member.name.eq(name),
            member.address.legion.eq(legion),
            member.address.city.eq(city),
            member.address.street.eq(street)
        )
        .fetchOne();

    return Optional.ofNullable(result);
  }
}
