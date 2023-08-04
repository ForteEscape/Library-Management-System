package com.management.library.service.scheduled;

import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.domain.book.Book;
import com.management.library.domain.type.BookStatus;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.repository.review.BookReviewRepository;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberServiceCreateDto;
import com.management.library.service.member.dto.MemberServiceCreateDto.Request;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.management.library.service.review.BookReviewService;
import com.management.library.service.review.dto.BookReviewServiceDto;
import java.time.LocalDate;
import java.time.YearMonth;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class ScheduledServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  @Autowired
  private MemberService memberService;
  @Autowired
  private BookService bookService;
  @Autowired
  private RentalService rentalService;
  @Autowired
  private ScheduledService scheduledService;
  @Autowired
  private BookReviewService bookReviewService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BookRentalRepository bookRentalRepository;
  @Autowired
  private BookReviewRepository bookReviewRepository;
  @Autowired
  private BookRepository bookRepository;

  private static final String MANAGEMENT_CACHE_KEY = "management-request-count:";
  private static final String NEW_BOOK_CACHE_KEY = "book-request-count:";
  private static final String BOOK_RENTED_COUNT = "book-rented-count";
  private static final String MONTHLY_RENTED_COUNT = "monthly-rented-count";
  private static final String MONTHLY_REVIEW_COUNT = "monthly-review-count";
  private static final String MONTHLY_BOOK_UNAVAILABLE_COUNT = "monthly-book-unavailable-count";
  private static final String YEARLY_RENTED_COUNT = "yearly-rented-count";
  private static final String YEARLY_REVIEW_COUNT = "yearly-review-count";
  private static final String YEARLY_BOOK_UNAVAILABLE_COUNT = "yearly-book-unavailable-count";
  private static final String REVIEW_CACHE_PREFIX = "review-member:";
  private static final String BOOK_REVIEW_RATE = "book-review-rate";
  private static final String BOOK_REVIEW_COUNT = "book-review-count";
  private static final String RENTAL_REDIS_KEY = "rental-count";
  private static final String PENALTY_MEMBER_KEY = "penalty:";

  @AfterEach
  void tearDown(){
    bookReviewRepository.deleteAllInBatch();
    bookRentalRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();

    redisTemplate.delete("memberCode");
    redisTemplate.delete(MANAGEMENT_CACHE_KEY);
    redisTemplate.delete(NEW_BOOK_CACHE_KEY);
    redisTemplate.delete(BOOK_RENTED_COUNT);
    redisTemplate.delete(MONTHLY_RENTED_COUNT);
    redisTemplate.delete(MONTHLY_REVIEW_COUNT);
    redisTemplate.delete(MONTHLY_BOOK_UNAVAILABLE_COUNT);
    redisTemplate.delete(YEARLY_RENTED_COUNT);
    redisTemplate.delete(YEARLY_REVIEW_COUNT);
    redisTemplate.delete(YEARLY_BOOK_UNAVAILABLE_COUNT);
    redisTemplate.delete(RENTAL_REDIS_KEY);
    redisTemplate.delete(BOOK_REVIEW_RATE);
    redisTemplate.delete(BOOK_REVIEW_COUNT);
    redisTemplate.delete(PENALTY_MEMBER_KEY);

    for (int i = 1; i < 100; i++) {
      String keyCode = String.valueOf(100000000 + i);
      redisTemplate.delete(REVIEW_CACHE_PREFIX + keyCode);
    }
  }

  @DisplayName("월간 대여 정산을 수행할 수 있다.")
  @Test
  public void monthlyRentalSettle() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    MemberServiceCreateDto.Response member = memberService.createMember(memberRequest);

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
    BookReviewServiceDto.Response review = bookReviewService.createReview("book1", reviewRequest,
        member.getMemberCode());

    // when
    scheduledService.monthlyRentalSettle();

    // then
    LocalDate startDate = YearMonth.now().minusMonths(2).atEndOfMonth();
    LocalDate endDate = YearMonth.now().atDay(1);

    int monthValue = endDate.getMonthValue() - 1;
    int year = startDate.getYear();

    if (monthValue == 0) {
      monthValue = 12;
    }

    String hashKey = year + "-" + monthValue;

    Object o = redisTemplate.opsForHash().get(MONTHLY_RENTED_COUNT, hashKey);
    assertThat(o).isNotNull();
    assertThat(String.valueOf(o)).isEqualTo("0");
  }

  @DisplayName("월간 리뷰 정산을 수행할 수 있다.")
  @Test
  public void monthlyReviewSettle() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    MemberServiceCreateDto.Response member = memberService.createMember(memberRequest);

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
    BookReviewServiceDto.Response review = bookReviewService.createReview("book1", reviewRequest,
        member.getMemberCode());

    // when
    scheduledService.monthlyReviewSettle();

    // then
    LocalDate startDate = YearMonth.now().minusMonths(2).atEndOfMonth();
    LocalDate endDate = YearMonth.now().atDay(1);

    int monthValue = endDate.getMonthValue() - 1;
    int year = startDate.getYear();

    if (monthValue == 0) {
      monthValue = 12;
    }

    String hashKey = year + "-" + monthValue;

    Object o = redisTemplate.opsForHash().get(MONTHLY_REVIEW_COUNT, hashKey);
    assertThat(o).isNotNull();
    assertThat(String.valueOf(o)).isEqualTo("0");
  }

  @DisplayName("월간 대여 정산을 수행할 수 있다.")
  @Test
  public void monthlyBookUnavailableSettle() throws Exception {
    // given
    Request memberRequest = createMemberRequest("kim", "980101", "legion", "city", "street");
    MemberServiceCreateDto.Response member = memberService.createMember(memberRequest);

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
    BookReviewServiceDto.Response review = bookReviewService.createReview("book1", reviewRequest,
        member.getMemberCode());

    Book book = bookRepository.findById(newBook.getId()).get();
    book.changeBookStatus(BookStatus.UNAVAILABLE);

    // when
    scheduledService.monthlyUnavailableBookSettle();

    // then
    LocalDate startDate = YearMonth.now().minusMonths(2).atEndOfMonth();
    LocalDate endDate = YearMonth.now().atDay(1);

    int monthValue = endDate.getMonthValue() - 1;
    int year = startDate.getYear();

    if (monthValue == 0) {
      monthValue = 12;
    }

    String hashKey = year + "-" + monthValue;

    Object o = redisTemplate.opsForHash().get(MONTHLY_BOOK_UNAVAILABLE_COUNT, hashKey);
    assertThat(o).isNotNull();
    assertThat(String.valueOf(o)).isEqualTo("1");
  }

  @DisplayName("연 대여 정산을 수행할 수 있다.")
  @Test
  public void yearlyRentSettle() throws Exception {
    // given
    for (int i = 1; i <= 12; i++){
      redisTemplate.opsForHash().put(MONTHLY_RENTED_COUNT, "2022-" + i, "3");
    }

    // when
    scheduledService.yearlyRentalSettle();

    // then
    Object o = redisTemplate.opsForHash().get(YEARLY_RENTED_COUNT, "2022");
    assertThat(o).isNotNull();
    assertThat(String.valueOf(o)).isEqualTo("36");
  }

  @DisplayName("연 리뷰 정산을 수행할 수 있다.")
  @Test
  public void yearlyReviewSettle() throws Exception {
    // given
    for (int i = 1; i <= 12; i++){
      redisTemplate.opsForHash().put(MONTHLY_REVIEW_COUNT, "2022-" + i, "3");
    }

    // when
    scheduledService.yearlyReviewSettle();

    // then
    Object o = redisTemplate.opsForHash().get(YEARLY_REVIEW_COUNT, "2022");
    assertThat(o).isNotNull();
    assertThat(String.valueOf(o)).isEqualTo("36");
  }

  @DisplayName("연 도서 손/망실 정산을 수행할 수 있다.")
  @Test
  public void yearlyBookUnavailableSettle() throws Exception {
    // given
    for (int i = 1; i <= 12; i++){
      redisTemplate.opsForHash().put(MONTHLY_BOOK_UNAVAILABLE_COUNT, "2022-" + i, "3");
    }

    // when
    scheduledService.yearlyBookUnavailableSettle();

    // then
    Object o = redisTemplate.opsForHash().get(YEARLY_BOOK_UNAVAILABLE_COUNT, "2022");
    assertThat(o).isNotNull();
    assertThat(String.valueOf(o)).isEqualTo("36");
  }

  private RentalBookInfoDto createRentalData(Response createdBook) {
    return RentalBookInfoDto.builder()
        .bookTitle(createdBook.getTitle())
        .author(createdBook.getAuthor())
        .build();
  }

  private MemberServiceCreateDto.Request createMemberRequest(String name,
      String birthdayCode, String legion, String city, String street) {
    return MemberServiceCreateDto.Request.builder()
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