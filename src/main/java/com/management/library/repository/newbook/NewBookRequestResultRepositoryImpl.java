package com.management.library.repository.newbook;

import static com.management.library.domain.admin.QAdministrator.administrator;
import static com.management.library.domain.newbook.QNewBookRequest.newBookRequest;
import static com.management.library.domain.newbook.QNewBookRequestResult.newBookRequestResult;
import static com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Response;

import com.management.library.domain.newbook.NewBookRequestResult;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class NewBookRequestResultRepositoryImpl implements NewBookRequestResultRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  public NewBookRequestResultRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }


  @Override
  public Optional<NewBookRequestResult> findByRequestId(Long requestId) {
    NewBookRequestResult result = queryFactory.selectFrom(newBookRequestResult)
        .join(newBookRequestResult.newBookRequest, newBookRequest)
        .where(newBookRequest.id.eq(requestId))
        .fetchOne();

    return Optional.ofNullable(result);
  }

  @Override
  public Page<Response> findByAdminId(String adminEmail, Pageable pageable) {
    List<NewBookRequestResult> result = queryFactory.selectFrom(newBookRequestResult)
        .join(newBookRequestResult.administrator, administrator).fetchJoin()
        .where(administrator.email.eq(adminEmail))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<Response> content = result.stream()
        .map(Response::of)
        .collect(Collectors.toList());

    JPAQuery<Long> countQuery = queryFactory.select(newBookRequestResult.count())
        .from(newBookRequestResult)
        .join(newBookRequestResult.administrator, administrator)
        .where(administrator.email.eq(adminEmail));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }
}
