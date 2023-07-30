package com.management.library.repository.rental;

import static com.management.library.domain.book.QBook.*;
import static com.management.library.domain.member.QMember.*;
import static com.management.library.domain.rental.QRental.*;

import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.RentalStatus;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
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
  public Page<RentalServiceResponseDto> findRentalPageByMemberCode(BookRentalSearchCond cond,
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
    List<RentalServiceResponseDto> result = rentals.stream()
        .map(RentalServiceResponseDto::of)
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
  public List<RentalServiceResponseDto> findRentalListByMemberCode(String memberCode) {
    List<Rental> result = queryFactory.selectFrom(rental)
        .join(rental.member, member).fetchJoin()
        .where(member.memberCode.eq(memberCode))
        .fetch();

    return result.stream()
        .map(RentalServiceResponseDto::of)
        .collect(Collectors.toList());
  }

  @Override
  public Page<RentalServiceResponseDto> findAllWithPage(BookRentalSearchCond cond,
      Pageable pageable) {
    List<Rental> result = queryFactory.selectFrom(rental)
        .join(rental.book, book).fetchJoin()
        .where(rentalStatusEq(cond.getRentalStatus()))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<RentalServiceResponseDto> content = result.stream()
        .map(RentalServiceResponseDto::of)
        .collect(Collectors.toList());

    JPAQuery<Long> countQuery = queryFactory.select(rental.count())
        .from(rental)
        .where(rentalStatusEq(cond.getRentalStatus()));

    return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
  }

  @Override
  public Optional<Rental> findByBookInfoAndStatus(String memberCode, String bookTitle,
      String author) {

    Rental result = queryFactory.select(rental)
        .from(rental)
        .join(rental.member, member).fetchJoin()
        .join(rental.book, book).fetchJoin()
        .where(
            rentalStatusEq(RentalStatus.PROCEEDING).or(rentalStatusEq(RentalStatus.OVERDUE)),
            member.memberCode.eq(memberCode),
            book.bookInfo.title.eq(bookTitle),
            book.bookInfo.author.eq(author)
        )
        .fetchOne();

    return Optional.ofNullable(result);
  }

  private BooleanExpression rentalStatusEq(RentalStatus rentalStatus) {
    return rentalStatus != null ? rental.rentalStatus.eq(rentalStatus) : null;
  }

}
