package com.management.library.repository.book;

import static com.management.library.domain.book.QBook.book;
import static com.querydsl.core.types.Projections.*;

import com.management.library.domain.book.Book;
import com.management.library.controller.book.dto.BookSearchCond;
import com.management.library.domain.type.BookStatus;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

public class BookRepositoryImpl implements BookRepositoryCustom {

  private final JPAQueryFactory queryFactory;

  public BookRepositoryImpl(EntityManager entityManager) {
    this.queryFactory = new JPAQueryFactory(entityManager);
  }

  /**
   * 도서의 이름, 저자, 출판사를 사용하여 검색이 가능 검색 결과는 페이징되어 반환
   *
   * @param cond     검색 조건
   * @param pageable 페이징 설정
   * @return 페이징된 검색 결과 DTO
   */
  @Override
  public Page<BookServiceCreateDto.Response> bookSearch(BookSearchCond cond, Pageable pageable) {
    List<Response> result = queryFactory.select(
            constructor(Response.class,
                book.id,
                book.bookInfo.title,
                book.bookInfo.author,
                book.bookInfo.publisher,
                book.bookInfo.publishedYear,
                book.bookInfo.location,
                book.typeCode,
                book.bookStatus
            )
        )
        .from(book)
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

  /**
   * 도서 분류 코드를 통해서 책들을 필터링할 수 있다.
   *
   * @param startCode 시작 분류 번호
   * @param endCode   끝 분류 번호
   * @param pageable  페이징 설정
   * @return 페이징된 결과 DTO
   */
  @Override
  public Page<BookServiceCreateDto.Response> findAllByBookTypeCode(int startCode, int endCode,
      Pageable pageable) {
    List<Response> result = queryFactory.select(
            constructor(Response.class,
                book.id,
                book.bookInfo.title,
                book.bookInfo.author,
                book.bookInfo.publisher,
                book.bookInfo.publishedYear,
                book.bookInfo.location,
                book.typeCode,
                book.bookStatus
            )
        )
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

  @Override
  public Optional<Book> findByTitleAndAuthor(String title, String author) {
    Book result = queryFactory.select(book)
        .from(book)
        .where(
            book.bookInfo.title.eq(title),
            book.bookInfo.author.eq(author)
        )
        .fetchOne();

    return Optional.ofNullable(result);
  }

  @Override
  public Long countByBookUnavailableStatus() {
    return queryFactory.select(book.count())
        .from(book)
        .where(book.bookStatus.eq(BookStatus.UNAVAILABLE))
        .fetchOne();
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
