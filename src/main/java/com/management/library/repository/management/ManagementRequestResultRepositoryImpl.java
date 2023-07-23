package com.management.library.repository.management;

import static com.management.library.domain.admin.QAdministrator.*;
import static com.management.library.domain.management.QManagementRequest.*;
import static com.management.library.domain.management.QManagementRequestResult.*;

import com.management.library.domain.management.ManagementRequestResult;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class ManagementRequestResultRepositoryImpl implements
    ManagementRequestResultRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public ManagementRequestResultRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Optional<ManagementRequestResult> findByRequestId(Long requestId) {
    ManagementRequestResult result = queryFactory.selectFrom(managementRequestResult)
        .join(managementRequestResult.managementRequest, managementRequest).fetchJoin()
        .where(managementRequest.id.eq(requestId))
        .fetchOne();

    return Optional.ofNullable(result);
  }

  @Override
  public Page<ManagementRequestResult> findByAdminEmail(String adminEmail, Pageable pageable) {
    List<ManagementRequestResult> result = queryFactory.selectFrom(managementRequestResult)
        .join(managementRequestResult.administrator, administrator).fetchJoin()
        .where(administrator.email.eq(adminEmail))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(managementRequestResult.count())
        .from(managementRequestResult)
        .join(managementRequestResult.administrator, administrator)
        .where(administrator.email.eq(adminEmail));

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }
}
