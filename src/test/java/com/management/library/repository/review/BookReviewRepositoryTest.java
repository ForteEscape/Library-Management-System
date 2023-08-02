package com.management.library.repository.review;

import static com.management.library.exception.ErrorCode.REVIEW_NOT_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.domain.book.Book;
import com.management.library.domain.book.BookInfo;
import com.management.library.domain.book.BookReview;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.type.Authority;
import com.management.library.domain.type.BookStatus;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.review.dto.BookReviewDetailDto;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class BookReviewRepositoryTest {

  @Autowired
  private BookReviewRepository bookReviewRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private MemberRepository memberRepository;

  @DisplayName("회원 번호로 도서 리뷰 목록을 찾을 수 있다.")
  @Test
  public void findByMemberCode() throws Exception {
    // given
    Member member = createMember("kim", "12345");
    memberRepository.save(member);

    Book book1 = createBook("book1", "author1", "publisher", "location", 2015, 135);
    Book book2 = createBook("book2", "author2", "publisher", "location", 2015, 135);
    Book book3 = createBook("book3", "author3", "publisher", "location", 2015, 135);

    bookRepository.saveAll(List.of(book1, book2, book3));

    BookReview bookReview1 = createBookReview(member, book1, "title1", "content1", 5);
    BookReview bookReview2 = createBookReview(member, book2, "title2", "content2", 4);
    BookReview bookReview3 = createBookReview(member, book3, "title3", "content3", 3);

    bookReviewRepository.saveAll(List.of(bookReview1, bookReview2, bookReview3));

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<BookReviewOverviewDto> result = bookReviewRepository.findByMemberCode(
        member.getMemberCode(), pageRequest);
    List<BookReviewOverviewDto> content = result.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("bookTitle", "reviewTitle", "rate")
        .contains(
            tuple("book1", "title1", 5),
            tuple("book2", "title2", 4),
            tuple("book3", "title3", 3)
        );
  }

  @DisplayName("리뷰와 책 이름을 리뷰 id로 조회할 수 있다.")
  @Test
  public void findReviewAndBookById() throws Exception {
    // given
    Member member = createMember("kim", "12345");
    memberRepository.save(member);

    Book book1 = createBook("book1", "author1", "publisher", "location", 2015, 135);
    Book book2 = createBook("book2", "author2", "publisher", "location", 2015, 135);
    Book book3 = createBook("book3", "author3", "publisher", "location", 2015, 135);

    bookRepository.saveAll(List.of(book1, book2, book3));

    BookReview bookReview1 = createBookReview(member, book1, "title1", "content1", 5);
    BookReview bookReview2 = createBookReview(member, book2, "title2", "content2", 4);
    BookReview bookReview3 = createBookReview(member, book3, "title3", "content3", 3);

    bookReviewRepository.saveAll(List.of(bookReview1, bookReview2, bookReview3));

    // when
    BookReview bookReview = bookReviewRepository.findReviewAndBookById(bookReview1.getId())
        .orElseThrow(() -> new NoSuchElementExistsException(REVIEW_NOT_EXISTS));

    BookReviewDetailDto result = BookReviewDetailDto.of(bookReview);

    // then
    assertThat(result)
        .extracting("bookTitle", "reviewTitle", "reviewContent", "rate")
        .contains(
            "book1", "title1", "content1", 5
        );
  }

  private BookReview createBookReview(Member member, Book book, String title, String content, int rate){
    return BookReview.builder()
        .member(member)
        .book(book)
        .reviewTitle(title)
        .reviewContent(content)
        .rate(rate)
        .build();
  }

  private Book createBook(String title, String author, String publisher, String location,
      int publishedYear, int typeCode) {
    BookInfo bookInfo = createBookInfo(title, author, publisher, location, publishedYear);

    return Book.builder()
        .bookInfo(bookInfo)
        .bookStatus(BookStatus.AVAILABLE)
        .typeCode(typeCode)
        .build();
  }

  private static Member createMember(String name, String memberCode) {
    Address address = Address.builder()
        .legion("경상남도")
        .city("김해시")
        .street("삼계로")
        .build();

    return Member.builder()
        .name(name)
        .birthdayCode("980101")
        .memberCode(memberCode)
        .address(address)
        .password("1234")
        .authority(Authority.ROLE_MEMBER)
        .build();
  }

  private BookInfo createBookInfo(String title, String author, String publisher, String location,
      int publishedYear) {
    return BookInfo.builder()
        .title(title)
        .author(author)
        .publisher(publisher)
        .location(location)
        .publishedYear(publishedYear)
        .build();
  }
}