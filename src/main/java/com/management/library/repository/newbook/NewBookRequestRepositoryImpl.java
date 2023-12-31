package com.management.library.repository.newbook;

import static com.management.library.domain.member.QMember.member;
import static com.management.library.domain.newbook.QNewBookRequest.newBookRequest;
import static com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;

import com.management.library.domain.newbook.NewBookRequest;
import com.management.library.domain.type.RequestStatus;
import com.management.library.controller.dto.RequestSearchCond;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class NewBookRequestRepositoryImpl implements NewBookRequestRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public NewBookRequestRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<Response> findByMemberCode(String memberCode, Pageable pageable) {
    List<NewBookRequest> result = queryFactory.selectFrom(newBookRequest)
        .join(newBookRequest.member, member).fetchJoin()
        .where(member.memberCode.eq(memberCode))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<Response> content = result.stream()
        .map(Response::of)
        .collect(Collectors.toList());

    JPAQuery<Long> countQuery = queryFactory.select(newBookRequest.count())
        .from(newBookRequest)
        .join(newBookRequest.member, member)
        .where(member.memberCode.eq(memberCode));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<Response> findAll(RequestSearchCond cond, Pageable pageable) {
    List<NewBookRequest> result = queryFactory.selectFrom(newBookRequest)
        .join(newBookRequest.member, member).fetchJoin()
        .where(requestStatusEq(cond.getRequestStatus()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<Response> content = result.stream()
        .map(Response::of)
        .collect(Collectors.toList());

    JPAQuery<Long> countQuery = queryFactory.select(newBookRequest.count())
        .from(newBookRequest)
        .where(requestStatusEq(cond.getRequestStatus()));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  private BooleanExpression requestStatusEq(RequestStatus requestStatus) {
    return requestStatus != null ? newBookRequest.requestStatus.eq(requestStatus) : null;
  }
}
