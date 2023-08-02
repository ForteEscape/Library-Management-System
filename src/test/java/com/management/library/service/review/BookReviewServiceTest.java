package com.management.library.service.review;

import static com.management.library.exception.ErrorCode.RETURNED_RENTAL_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.REVIEW_ALREADY_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.repository.review.BookReviewRepository;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.member.dto.MemberCreateServiceDto.Request;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.management.library.service.review.dto.BookReviewDetailDto;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import com.management.library.service.review.dto.BookReviewServiceDto;
import com.management.library.service.review.dto.BookReviewUpdateDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BookReviewServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private BookReviewService bookReviewService;
  @Autowired
  private MemberService memberService;
  @Autowired
  private BookService bookService;
  @Autowired
  private RentalService rentalService;

  @Autowired
  private BookReviewRepository bookReviewRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private BookRentalRepository bookRentalRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private static final String RENTAL_REDIS_KEY = "rental-count";
  private static final String BOOK_RENTED_COUNT = "book-rented-count";
  private static final String REVIEW_CACHE_PREFIX = "review-member:";
  private static final String BOOK_REVIEW_RATE = "book-review-rate";
  private static final String BOOK_REVIEW_COUNT = "book-review-count";

  @AfterEach
  void tearDown() {
    bookReviewRepository.deleteAllInBatch();
    bookRentalRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();

    redisTemplate.delete("memberCode");
    redisTemplate.delete(BOOK_REVIEW_RATE);
    redisTemplate.delete(RENTAL_REDIS_KEY);
    redisTemplate.delete(BOOK_RENTED_COUNT);
    redisTemplate.delete(BOOK_REVIEW_COUNT);

    for (int i = 1; i < 100; i++) {
      String keyCode = String.valueOf(100000000 + i);
      redisTemplate.delete(REVIEW_CACHE_PREFIX + keyCode);
    }
  }

  @DisplayName("도서에 대한 리뷰를 등록할 수 있다.")
  @Test
  public void createReview() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    MemberCreateServiceDto.Response member = memberService.createMember(memberRequest);

    BookServiceCreateDto.Request bookRequest = createBookRequest("book1", "park", "publisher", 2015,
        "location", 130);
    Response newBook = bookService.createNewBook(bookRequest);

    RentalBookInfoDto rentalData = createRentalData(newBook);
    LocalDate rentedDate = LocalDate.now().minusDays(2);
    RentalServiceResponseDto bookRental = rentalService.createBookRental(member.getMemberCode(),
        rentalData, rentedDate);

    rentalService.returnBook(member.getMemberCode(), bookRequest.getTitle(),
        bookRequest.getAuthor());

    BookReviewServiceDto.Request reviewRequest = createReviewRequest("review", "reviewContent", 5);

    // when
    BookReviewServiceDto.Response review = bookReviewService.createReview("book1", reviewRequest,
        member.getMemberCode());

    // then
    assertThat(review)
        .extracting("reviewTitle", "reviewContent", "reviewRate")
        .contains(
            "review", "reviewContent", 5
        );
  }

  @DisplayName("리뷰할 도서에 대한 반납 기록이 없다면 리뷰를 등록할 수 없다.")
  @Test
  public void createReviewWithoutRental() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    MemberCreateServiceDto.Response member = memberService.createMember(memberRequest);

    BookServiceCreateDto.Request bookRequest = createBookRequest("book1", "park", "publisher", 2015,
        "location", 130);
    Response newBook = bookService.createNewBook(bookRequest);

    RentalBookInfoDto rentalData = createRentalData(newBook);
    LocalDate rentedDate = LocalDate.now().minusDays(2);
    RentalServiceResponseDto bookRental = rentalService.createBookRental(member.getMemberCode(),
        rentalData, rentedDate);

    BookReviewServiceDto.Request reviewRequest = createReviewRequest("review", "reviewContent", 5);

    // when
    // then
    assertThatThrownBy(() -> bookReviewService.createReview("book1", reviewRequest,
        member.getMemberCode()))
        .isInstanceOf(NoSuchElementExistsException.class)
        .extracting("errorCode", "description")
        .contains(
            RETURNED_RENTAL_NOT_EXISTS, RETURNED_RENTAL_NOT_EXISTS.getDescription()
        );
  }

  @DisplayName("하나의 도서를 2번 리뷰할 수 없다.")
  @Test
  public void createReviewWithTwice() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    MemberCreateServiceDto.Response member = memberService.createMember(memberRequest);

    BookServiceCreateDto.Request bookRequest = createBookRequest("book1", "park", "publisher", 2015,
        "location", 130);
    Response newBook = bookService.createNewBook(bookRequest);

    RentalBookInfoDto rentalData = createRentalData(newBook);
    LocalDate rentedDate = LocalDate.now().minusDays(2);
    RentalServiceResponseDto bookRental = rentalService.createBookRental(member.getMemberCode(),
        rentalData, rentedDate);

    rentalService.returnBook(member.getMemberCode(), bookRequest.getTitle(),
        bookRequest.getAuthor());

    BookReviewServiceDto.Request reviewRequest = createReviewRequest("review", "reviewContent", 5);
    BookReviewServiceDto.Request reviewRequest2 = createReviewRequest("review2", "reviewContent2",
        4);

    bookReviewService.createReview("book1", reviewRequest, member.getMemberCode());

    // when
    // then
    assertThatThrownBy(() -> bookReviewService.createReview("book1", reviewRequest2,
        member.getMemberCode()))
        .isInstanceOf(DuplicateException.class)
        .extracting("errorCode", "description")
        .contains(
            REVIEW_ALREADY_EXISTS, REVIEW_ALREADY_EXISTS.getDescription()
        );
  }

  @DisplayName("리뷰의 재목과 내용을 변경할 수 있다.")
  @Test
  public void updateReview() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    MemberCreateServiceDto.Response member = memberService.createMember(memberRequest);

    BookServiceCreateDto.Request bookRequest = createBookRequest("book1", "park", "publisher", 2015,
        "location", 130);
    Response newBook = bookService.createNewBook(bookRequest);

    RentalBookInfoDto rentalData = createRentalData(newBook);
    LocalDate rentedDate = LocalDate.now().minusDays(2);
    RentalServiceResponseDto bookRental = rentalService.createBookRental(member.getMemberCode(),
        rentalData, rentedDate);

    rentalService.returnBook(member.getMemberCode(), bookRequest.getTitle(),
        bookRequest.getAuthor());

    BookReviewServiceDto.Request reviewRequest = createReviewRequest("review", "reviewContent", 5);
    BookReviewServiceDto.Response review = bookReviewService.createReview("book1",
        reviewRequest, member.getMemberCode());

    BookReviewUpdateDto.Request updateReviewRequest = BookReviewUpdateDto.Request.builder()
        .updateReviewTitle("review2")
        .updateReviewContent("reviewContent2")
        .build();

    // when
    BookReviewUpdateDto.Response result = bookReviewService.updateReview(updateReviewRequest,
        review.getId());

    // then
    assertThat(result)
        .extracting("reviewTitle", "reviewContent", "rate")
        .contains(
            "review2", "reviewContent2", 5
        );
  }

  @DisplayName("특정 회원이 등록한 모든 리뷰 기록 리스트를 반환할 수 있다.")
  @Test
  public void getReviewListByMemberCode() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    MemberCreateServiceDto.Response member = memberService.createMember(memberRequest);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("book1", "park", "publisher",
        2015, "location", 130);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("book2", "lee", "publisher", 2015,
        "location", 130);
    BookServiceCreateDto.Request bookRequest3 = createBookRequest("book3", "han", "publisher", 2015,
        "location", 130);
    Response newBook1 = bookService.createNewBook(bookRequest1);
    Response newBook2 = bookService.createNewBook(bookRequest2);
    Response newBook3 = bookService.createNewBook(bookRequest3);

    LocalDate rentedDate = LocalDate.now().minusDays(2);
    LocalDate rentedDate2 = LocalDate.now().minusDays(1);

    RentalBookInfoDto rentalData1 = createRentalData(newBook1);
    RentalServiceResponseDto bookRental1 = rentalService.createBookRental(member.getMemberCode(),
        rentalData1, rentedDate);

    RentalBookInfoDto rentalData2 = createRentalData(newBook2);
    RentalServiceResponseDto bookRental2 = rentalService.createBookRental(member.getMemberCode(),
        rentalData2, rentedDate);

    rentalService.returnBook(member.getMemberCode(), bookRequest1.getTitle(),
        bookRequest1.getAuthor());

    rentalService.returnBook(member.getMemberCode(), bookRequest2.getTitle(),
        bookRequest2.getAuthor());

    RentalBookInfoDto rentalData3 = createRentalData(newBook3);
    RentalServiceResponseDto bookRental3 = rentalService.createBookRental(member.getMemberCode(),
        rentalData3, rentedDate2);

    rentalService.returnBook(member.getMemberCode(), bookRequest3.getTitle(),
        bookRequest3.getAuthor());

    BookReviewServiceDto.Request reviewRequest1 = createReviewRequest("review1", "reviewContent1",
        5);
    BookReviewServiceDto.Request reviewRequest2 = createReviewRequest("review2", "reviewContent2",
        4);
    BookReviewServiceDto.Request reviewRequest3 = createReviewRequest("review3", "reviewContent3",
        5);

    bookReviewService.createReview("book1", reviewRequest1, member.getMemberCode());
    bookReviewService.createReview("book2", reviewRequest2, member.getMemberCode());
    bookReviewService.createReview("book3", reviewRequest3, member.getMemberCode());

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<BookReviewOverviewDto> result = bookReviewService.getMemberReviewDataList(
        member.getMemberCode(), pageRequest);
    List<BookReviewOverviewDto> content = result.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("bookTitle", "reviewTitle", "rate")
        .contains(
            tuple("book1", "review1", 5),
            tuple("book2", "review2", 4),
            tuple("book3", "review3", 5)
        );
  }

  @DisplayName("회원이 작성한 리뷰 데이터 단건을 조회할 수 있다.")
  @Test
  public void getMemberReviewData() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    MemberCreateServiceDto.Response member = memberService.createMember(memberRequest);

    BookServiceCreateDto.Request bookRequest = createBookRequest("book1", "park", "publisher", 2015,
        "location", 130);
    Response newBook = bookService.createNewBook(bookRequest);

    RentalBookInfoDto rentalData = createRentalData(newBook);
    LocalDate rentedDate = LocalDate.now().minusDays(2);
    RentalServiceResponseDto bookRental = rentalService.createBookRental(member.getMemberCode(),
        rentalData, rentedDate);

    rentalService.returnBook(member.getMemberCode(), bookRequest.getTitle(),
        bookRequest.getAuthor());

    BookReviewServiceDto.Request reviewRequest = createReviewRequest("review", "reviewContent", 5);
    BookReviewServiceDto.Response review = bookReviewService.createReview("book1",
        reviewRequest, member.getMemberCode());

    // when
    BookReviewDetailDto response = bookReviewService.getReviewData(review.getId());

    // then
    assertThat(response)
        .extracting("bookTitle", "reviewTitle", "reviewContent", "rate")
        .contains(
            "book1", "review", "reviewContent", 5
        );
  }

  @DisplayName("도서의 이름으로 리뷰를 조회할 수 있다.")
  @Test
  public void getBookReviewList() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    Request memberRequest2 = createMemberRequest("ju", "980101", "legion", "city", "street");
    MemberCreateServiceDto.Response member = memberService.createMember(memberRequest);
    MemberCreateServiceDto.Response member2 = memberService.createMember(memberRequest2);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("book1", "park", "publisher",
        2015, "location", 130);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("book2", "lee", "publisher", 2015,
        "location", 130);
    BookServiceCreateDto.Request bookRequest3 = createBookRequest("book3", "han", "publisher", 2015,
        "location", 130);
    Response newBook1 = bookService.createNewBook(bookRequest1);
    Response newBook2 = bookService.createNewBook(bookRequest2);
    Response newBook3 = bookService.createNewBook(bookRequest3);

    LocalDate rentedDate = LocalDate.now().minusDays(2);
    LocalDate rentedDate2 = LocalDate.now().minusDays(1);

    RentalBookInfoDto rentalData1 = createRentalData(newBook1);
    rentalService.createBookRental(member.getMemberCode(), rentalData1, rentedDate);

    RentalBookInfoDto rentalData2 = createRentalData(newBook2);
    rentalService.createBookRental(member.getMemberCode(), rentalData2, rentedDate);

    rentalService.returnBook(member.getMemberCode(), bookRequest1.getTitle(),
        bookRequest1.getAuthor());

    rentalService.returnBook(member.getMemberCode(), bookRequest2.getTitle(),
        bookRequest2.getAuthor());

    RentalBookInfoDto rentalData3 = createRentalData(newBook3);
    rentalService.createBookRental(member.getMemberCode(), rentalData3, rentedDate2);

    rentalService.returnBook(member.getMemberCode(), bookRequest3.getTitle(),
        bookRequest3.getAuthor());

    // 회원 2의 대여
    rentalService.createBookRental(member2.getMemberCode(), rentalData1, rentedDate);
    rentalService.returnBook(member2.getMemberCode(), rentalData1.getBookTitle(),
        rentalData1.getAuthor());

    BookReviewServiceDto.Request reviewRequest1 = createReviewRequest("review1", "reviewContent1",
        5);
    BookReviewServiceDto.Request reviewRequest2 = createReviewRequest("review2", "reviewContent2",
        4);
    BookReviewServiceDto.Request reviewRequest3 = createReviewRequest("review3", "reviewContent3",
        5);
    BookReviewServiceDto.Request reviewRequest4 = createReviewRequest("review4", "reviewContent4",
        5);

    bookReviewService.createReview("book1", reviewRequest1, member.getMemberCode());
    bookReviewService.createReview("book2", reviewRequest2, member.getMemberCode());
    bookReviewService.createReview("book3", reviewRequest3, member.getMemberCode());
    bookReviewService.createReview("book1", reviewRequest4, member2.getMemberCode());

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<BookReviewOverviewDto> result = bookReviewService.getBookReviewList(newBook1.getId(), pageRequest);
    List<BookReviewOverviewDto> content = result.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("bookTitle", "reviewTitle", "rate")
        .containsExactlyInAnyOrder(
            tuple("book1", "review1", 5),
            tuple("book1", "review4", 5)
        );
  }

  private RentalBookInfoDto createRentalData(Response createdBook) {
    return RentalBookInfoDto.builder()
        .bookTitle(createdBook.getTitle())
        .author(createdBook.getAuthor())
        .build();
  }

  private MemberCreateServiceDto.Request createMemberRequest(String name,
      String birthdayCode, String legion, String city, String street) {
    return MemberCreateServiceDto.Request.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }

  private BookServiceCreateDto.Request createBookRequest(String title, String author,
      String publisher, int publishedYear, String location, int typeCode) {
    return BookServiceCreateDto.Request.builder()
        .title(title)
        .author(author)
        .publisher(publisher)
        .publishedYear(publishedYear)
        .location(location)
        .typeCode(typeCode)
        .build();
  }

  private static BookReviewServiceDto.Request createReviewRequest(String reviewTitle,
      String reviewContent, int reviewRate) {
    return BookReviewServiceDto.Request.builder()
        .reviewTitle(reviewTitle)
        .reviewContent(reviewContent)
        .reviewRate(reviewRate)
        .build();
  }
}