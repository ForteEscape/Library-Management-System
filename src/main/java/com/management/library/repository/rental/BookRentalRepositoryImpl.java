package com.management.library.repository.rental;

import static com.management.library.domain.book.QBook.*;
import static com.management.library.domain.member.QMember.*;
import static com.management.library.domain.rental.QRental.*;

import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.RentalStatus;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.service.rental.dto.RentalServiceReadDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class BookRentalRepositoryImpl implements BookRentalRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public BookRentalRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  @Override
  public Page<RentalServiceReadDto> findRentalPageByMemberCode(BookRentalSearchCond cond,
      String memberCode, Pageable pageable) {

    // projections cannot use to direct dto query
    List<Rental> rentals = queryFactory.selectFrom(rental)
        .join(rental.member, member).fetchJoin()
        .join(rental.book, book).fetchJoin()
        .where(
            rentalStatusEq(cond.getRentalStatus()),
            member.memberCode.eq(memberCode)
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    // convert entity to dto
    List<RentalServiceReadDto> result = rentals.stream()
        .map(RentalServiceReadDto::of)
        .collect(Collectors.toList());

    JPAQuery<Long> countQuery = queryFactory.select(rental.count())
        .from(rental)
        .join(rental.member, member)
        .join(rental.book, book)
        .where(
            rentalStatusEq(cond.getRentalStatus()),
            member.memberCode.eq(memberCode)
        );

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  @Override
  public List<Rental> findRentalListByMemberCode(String memberCode) {
    return queryFactory.selectFrom(rental)
        .join(rental.member, member).fetchJoin()
        .where(member.memberCode.eq(memberCode))
        .fetch();
  }

  @Override
  public Page<Rental> findAllWithPage(BookRentalSearchCond cond, Pageable pageable) {
    List<Rental> result = queryFactory.selectFrom(rental)
        .where(rentalStatusEq(cond.getRentalStatus()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(rental.count())
        .from(rental)
        .where(rentalStatusEq(cond.getRentalStatus()));

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  private BooleanExpression rentalStatusEq(RentalStatus rentalStatus) {
    return rentalStatus != null ? rental.rentalStatus.eq(rentalStatus) : null;
  }

}
