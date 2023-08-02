package com.management.library.repository.member;

import static com.management.library.domain.member.QMember.member;
import static com.querydsl.core.types.Projections.constructor;

import com.management.library.controller.admin.dto.MemberSearchCond;
import com.management.library.domain.member.Member;
import com.management.library.service.member.dto.MemberReadServiceDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

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

  @Override
  public Page<MemberReadServiceDto> findAll(MemberSearchCond cond, Pageable pageable) {
    List<MemberReadServiceDto> result = queryFactory.select(constructor(
                MemberReadServiceDto.class,
                member.name,
                member.memberCode,
                member.birthdayCode,
                member.address.legion,
                member.address.city,
                member.address.street
            )
        )
        .from(member)
        .where(
            memberNameEq(cond.getMemberName()),
            memberCodeEq(cond.getMemberCode())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(member.count())
        .from(member)
        .where(
            memberNameEq(cond.getMemberName()),
            memberCodeEq(cond.getMemberCode())
        );

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  private BooleanExpression memberCodeEq(String memberCode) {
    return memberCode != null ? member.memberCode.contains(memberCode) : null;
  }

  private BooleanExpression memberNameEq(String memberName) {
    return memberName != null ? member.name.contains(memberName) : null;
  }


}
