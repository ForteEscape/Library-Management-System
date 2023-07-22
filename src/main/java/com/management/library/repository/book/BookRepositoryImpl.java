package com.management.library.repository.book;

import static com.management.library.domain.QBook.*;

import com.management.library.domain.QBook;
import com.management.library.domain.book.Book;
import com.management.library.dto.BookSearchCond;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class BookRepositoryImpl implements BookRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  public BookRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  /**
   * 도서의 이름, 저자, 출판사를 사용하여 검색이 가능
   * 검색 결과는 페이징되어 반환
   * @param cond 검색 조건
   * @param pageable 페이징 설정
   * @return 페이징된 검색 결과
   */
  @Override
  public Page<Book> bookSearch(BookSearchCond cond, Pageable pageable) {
    List<Book> result = queryFactory.selectFrom(book)
        .where(
            bookNameEq(cond.getBookTitle()),
            bookAuthorEq(cond.getBookAuthor()),
            bookPublisherEq(cond.getPublisherName())
        )
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(book.count())
        .from(book)
        .where(
            bookNameEq(cond.getBookTitle()),
            bookAuthorEq(cond.getBookAuthor()),
            bookPublisherEq(cond.getPublisherName())
        );

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  @Override
  public Page<Book> findAllByBookTypeCode(int startCode, int endCode, Pageable pageable) {
    List<Book> result = queryFactory.select(book)
        .from(book)
        .where(book.typeCode.between(startCode, endCode))
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    JPAQuery<Long> countQuery = queryFactory.select(book.count())
        .from(book)
        .where(book.typeCode.between(startCode, endCode));

    return PageableExecutionUtils.getPage(result, pageable, countQuery::fetchOne);
  }

  private BooleanExpression bookPublisherEq(String publisherName) {
    return publisherName != null ? book.bookInfo.publisher.eq(publisherName) : null;
  }

  private BooleanExpression bookAuthorEq(String bookAuthor) {
    return bookAuthor != null ? book.bookInfo.author.eq(bookAuthor) : null;
  }

  private BooleanExpression bookNameEq(String bookTitle) {
    return bookTitle != null ? book.bookInfo.title.contains(bookTitle) : null;
  }
}
