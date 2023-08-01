package com.management.library.repository.review;

import static com.management.library.domain.book.QBook.book;
import static com.management.library.domain.book.QBookReview.bookReview;
import static com.management.library.domain.member.QMember.member;

import com.management.library.domain.book.BookReview;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class BookReviewRepositoryImpl implements BookReviewRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  public BookReviewRepositoryImpl(EntityManager em){
    queryFactory = new JPAQueryFactory(em);
  }

  @Override
  public Page<BookReviewOverviewDto> findByMemberCode(String memberCode, Pageable pageable) {
    List<BookReview> contents = queryFactory.selectFrom(bookReview)
        .join(bookReview.book, book).fetchJoin()
        .join(bookReview.member, member).fetchJoin()
        .where(member.memberCode.eq(memberCode))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    List<BookReviewOverviewDto> result = contents.stream()
        .map(BookReviewOverviewDto::of)
        .collect(Collectors.toList());

    JPAQuery<Long> countQuery = queryFactory.select(bookReview.count())
        .from(bookReview)
        .join(bookReview.member, member).fetchJoin()
        .where(member.memberCode.eq(memberCode));

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  @Override
  public Optional<BookReview> findReviewAndBookById(Long id) {
    BookReview result = queryFactory.selectFrom(bookReview)
        .join(bookReview.book, book).fetchJoin()
        .where(bookReview.id.eq(id))
        .fetchOne();

    return Optional.ofNullable(result);
  }
}
