package com.management.library.repository.management;

import static com.management.library.domain.management.QManagementRequest.managementRequest;
import static com.management.library.domain.member.QMember.member;

import com.management.library.domain.management.ManagementRequest;
import com.management.library.domain.type.RequestStatus;
import com.management.library.dto.RequestSearchCond;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class ManagementRequestRepositoryImpl implements ManagementRequestRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public ManagementRequestRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<Response> findByMemberCode(String memberCode, Pageable pageable) {
    List<ManagementRequest> requests = queryFactory.selectFrom(managementRequest)
        .join(managementRequest.member, member).fetchJoin()
        .where(member.memberCode.eq(memberCode))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<Response> request = requests.stream()
        .map(Response::of)
        .collect(Collectors.toList());

    JPAQuery<Long> countQuery = queryFactory.select(managementRequest.count())
        .from(managementRequest)
        .join(managementRequest.member, member)
        .where(member.memberCode.eq(memberCode));

    return PageableExecutionUtils.getPage(request, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<ManagementRequest> findAll(RequestSearchCond cond, Pageable pageable) {
    List<ManagementRequest> request = queryFactory.selectFrom(managementRequest)
        .where(requestStatusEq(cond.getRequestStatus()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(managementRequest.count())
        .from(managementRequest)
        .where(requestStatusEq(cond.getRequestStatus()));

    return PageableExecutionUtils.getPage(request, pageable, countQuery::fetchOne);
  }

  private BooleanExpression requestStatusEq(RequestStatus requestStatus){
    return requestStatus != null ? managementRequest.requestStatus.eq(requestStatus) : null;
  }
}
