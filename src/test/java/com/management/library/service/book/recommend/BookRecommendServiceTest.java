package com.management.library.service.book.recommend;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.repository.review.BookReviewRepository;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.book.recommend.dto.BookRecommendResponseDto.RentedCount;
import com.management.library.service.book.recommend.dto.BookRecommendResponseDto.ReviewRate;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.management.library.service.review.BookReviewService;
import com.management.library.service.review.dto.BookReviewServiceDto;
import com.management.library.service.review.dto.BookReviewServiceDto.Request;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

/**
 * 현재 도서 리뷰 관련해서 아직 기능 구현을 수행하지 않았기 때문에 도서 리뷰 평점을 사용하여 추천 리스트를
 * 받는 기능 테스트는 이후 리뷰 기능이 구현되고 난 후에 수행
 */

@SpringBootTest
@Transactional
class BookRecommendServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private MemberService memberService;
  @Autowired
  private BookService bookService;
  @Autowired
  private RentalService rentalService;
  @Autowired
  private BookRecommendService bookRecommendService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private BookRentalRepository bookRentalRepository;
  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Autowired
  private BookReviewService bookReviewService;
  @Autowired
  private BookReviewRepository bookReviewRepository;

  private static final String RENTAL_REDIS_KEY = "rental-count";
  private static final String BOOK_RENTED_COUNT = "book-rented-count";
  private static final String BOOK_REVIEW_RATE = "book-review-rate";
  private static final String BOOK_REVIEW_COUNT = "book-review-count";
  private static final String REVIEW_CACHE_PREFIX = "review-member:";

  @AfterEach
  void tearDown(){
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

  @DisplayName("도서 대여 횟수를 기준으로 한 도서 추천 목록을 가져올 수 있다.")
  @Test
  public void getRecommendBookListByRentalCount() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("ju", "980101", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response createdMember1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response createdMember2 = memberService.createMember(memberRequest2);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "park", "publisher", 2017,
        "location", 835);
    BookServiceCreateDto.Request bookRequest3 = createBookRequest("spring", "lee", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest4 = createBookRequest("spring2", "lee", "publisher", 2017,
        "location", 835);
    BookServiceCreateDto.Request bookRequest5 = createBookRequest("docker", "han", "publisher", 2020,
        "location", 835);

    BookServiceCreateDto.Response createdBook1 = bookService.createNewBook(bookRequest1);
    BookServiceCreateDto.Response createdBook2 = bookService.createNewBook(bookRequest2);
    BookServiceCreateDto.Response createdBook3 = bookService.createNewBook(bookRequest3);
    BookServiceCreateDto.Response createdBook4 = bookService.createNewBook(bookRequest4);
    BookServiceCreateDto.Response createdBook5 = bookService.createNewBook(bookRequest5);

    RentalBookInfoDto rentalData1 = createRentalData(createdBook1);
    RentalBookInfoDto rentalData2 = createRentalData(createdBook2);
    RentalBookInfoDto rentalData3 = createRentalData(createdBook3);
    RentalBookInfoDto rentalData4 = createRentalData(createdBook4);
    RentalBookInfoDto rentalData5 = createRentalData(createdBook5);

    LocalDate rentedDate1 = LocalDate.now().minusDays(3);
    LocalDate rentedDate2 = LocalDate.now().minusDays(5);
    LocalDate rentedDate3 = LocalDate.now().minusDays(1);

    RentalServiceResponseDto bookRental1 = rentalService.createBookRental(
        createdMember1.getMemberCode(), rentalData1, rentedDate1);

    rentalService.createBookRental(createdMember1.getMemberCode(), rentalData2, rentedDate2);

    RentalServiceResponseDto bookRental2 = rentalService.createBookRental(
        createdMember2.getMemberCode(), rentalData3, rentedDate1);

    rentalService.createBookRental(createdMember2.getMemberCode(), rentalData4, rentedDate2);

    rentalService.returnBook(createdMember1.getMemberCode(), rentalData1.getBookTitle(), rentalData1.getAuthor());
    rentalService.returnBook(createdMember2.getMemberCode(), rentalData3.getBookTitle(), rentalData3.getAuthor());

    rentalService.createBookRental(createdMember1.getMemberCode(), rentalData5, rentedDate3);
    rentalService.createBookRental(createdMember2.getMemberCode(), rentalData1, rentedDate3);

    // when
    List<RentedCount> result = bookRecommendService.getRecommendBookListByRentalCount();

    // then
    assertThat(result).hasSize(5)
        .extracting("bookTitle", "rentedCount")
        .containsExactlyInAnyOrder(
            tuple("jpa", "2"),
            tuple("jpa2", "1"),
            tuple("spring", "1"),
            tuple("spring2", "1"),
            tuple("docker", "1")
        );
  }

  @DisplayName("도서 대여 기록이 없는 상태에서는 빈 리스트를 반환한다.")
  @Test
  public void getRecommendBookListByRentalCountWithEmpty() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("ju", "980101", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response createdMember1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response createdMember2 = memberService.createMember(memberRequest2);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "park", "publisher", 2017,
        "location", 835);
    BookServiceCreateDto.Request bookRequest3 = createBookRequest("spring", "lee", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest4 = createBookRequest("spring2", "lee", "publisher", 2017,
        "location", 835);
    BookServiceCreateDto.Request bookRequest5 = createBookRequest("docker", "han", "publisher", 2020,
        "location", 835);

    BookServiceCreateDto.Response createdBook1 = bookService.createNewBook(bookRequest1);
    BookServiceCreateDto.Response createdBook2 = bookService.createNewBook(bookRequest2);
    BookServiceCreateDto.Response createdBook3 = bookService.createNewBook(bookRequest3);
    BookServiceCreateDto.Response createdBook4 = bookService.createNewBook(bookRequest4);
    BookServiceCreateDto.Response createdBook5 = bookService.createNewBook(bookRequest5);

    // when
    List<RentedCount> result = bookRecommendService.getRecommendBookListByRentalCount();

    // then
    assertThat(result).isEmpty();
  }

  @DisplayName("")
  @Test
  public void getRecommendBookByBookRate() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("ju", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest3 = createMemberRequest("gang", "980101", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response createdMember1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response createdMember2 = memberService.createMember(memberRequest2);
    MemberCreateServiceDto.Response createdMember3 = memberService.createMember(memberRequest3);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "park", "publisher", 2017,
        "location", 835);
    BookServiceCreateDto.Request bookRequest3 = createBookRequest("spring", "lee", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest4 = createBookRequest("spring2", "lee", "publisher", 2017,
        "location", 835);
    BookServiceCreateDto.Request bookRequest5 = createBookRequest("docker", "han", "publisher", 2020,
        "location", 835);

    BookServiceCreateDto.Response createdBook1 = bookService.createNewBook(bookRequest1);
    BookServiceCreateDto.Response createdBook2 = bookService.createNewBook(bookRequest2);
    BookServiceCreateDto.Response createdBook3 = bookService.createNewBook(bookRequest3);
    BookServiceCreateDto.Response createdBook4 = bookService.createNewBook(bookRequest4);
    BookServiceCreateDto.Response createdBook5 = bookService.createNewBook(bookRequest5);

    RentalBookInfoDto rentalData1 = createRentalData(createdBook1);
    RentalBookInfoDto rentalData2 = createRentalData(createdBook2);
    RentalBookInfoDto rentalData3 = createRentalData(createdBook3);
    RentalBookInfoDto rentalData4 = createRentalData(createdBook4);
    RentalBookInfoDto rentalData5 = createRentalData(createdBook5);

    LocalDate rentedDate1 = LocalDate.now().minusDays(3);
    LocalDate rentedDate2 = LocalDate.now().minusDays(5);
    LocalDate rentedDate3 = LocalDate.now().minusDays(1);

    rentalService.createBookRental(createdMember1.getMemberCode(), rentalData1, rentedDate1);
    rentalService.createBookRental(createdMember1.getMemberCode(), rentalData2, rentedDate2);
    rentalService.createBookRental(createdMember2.getMemberCode(), rentalData3, rentedDate1);
    rentalService.createBookRental(createdMember2.getMemberCode(), rentalData4, rentedDate2);
    rentalService.createBookRental(createdMember3.getMemberCode(), rentalData5, rentedDate1);

    rentalService.returnBook(createdMember1.getMemberCode(), rentalData1.getBookTitle(), rentalData1.getAuthor());
    rentalService.returnBook(createdMember1.getMemberCode(), rentalData2.getBookTitle(), rentalData2.getAuthor());
    rentalService.returnBook(createdMember2.getMemberCode(), rentalData3.getBookTitle(), rentalData3.getAuthor());
    rentalService.returnBook(createdMember2.getMemberCode(), rentalData4.getBookTitle(), rentalData4.getAuthor());
    rentalService.returnBook(createdMember3.getMemberCode(), rentalData5.getBookTitle(), rentalData5.getAuthor());

    rentalService.createBookRental(createdMember1.getMemberCode(), rentalData5, rentedDate3);
    rentalService.createBookRental(createdMember2.getMemberCode(), rentalData1, rentedDate3);
    rentalService.createBookRental(createdMember3.getMemberCode(), rentalData2, rentedDate3);

    rentalService.returnBook(createdMember1.getMemberCode(), rentalData5.getBookTitle(), rentalData5.getAuthor());
    rentalService.returnBook(createdMember2.getMemberCode(), rentalData1.getBookTitle(), rentalData1.getAuthor());
    rentalService.returnBook(createdMember3.getMemberCode(), rentalData2.getBookTitle(), rentalData2.getAuthor());

    Request reviewRequest1 = createReviewRequest("review1", "review content1", 5);
    Request reviewRequest2 = createReviewRequest("review2", "review content2", 4);
    Request reviewRequest3 = createReviewRequest("review3", "review content3", 3);
    Request reviewRequest4 = createReviewRequest("review4", "review content4", 5);
    Request reviewRequest5 = createReviewRequest("review5", "review content5", 4);
    Request reviewRequest6 = createReviewRequest("review6", "review content6", 3);
    Request reviewRequest7 = createReviewRequest("review7", "review content7", 4);
    Request reviewRequest8 = createReviewRequest("review8", "review content8", 5);

    bookReviewService.createReview("jpa", reviewRequest1, createdMember1.getMemberCode());
    bookReviewService.createReview("jpa2", reviewRequest2, createdMember1.getMemberCode());
    bookReviewService.createReview("spring", reviewRequest3, createdMember2.getMemberCode());
    bookReviewService.createReview("spring2", reviewRequest4, createdMember2.getMemberCode());
    bookReviewService.createReview("docker", reviewRequest5, createdMember3.getMemberCode());
    bookReviewService.createReview("docker", reviewRequest6, createdMember1.getMemberCode());
    bookReviewService.createReview("jpa", reviewRequest7, createdMember2.getMemberCode());
    bookReviewService.createReview("jpa2", reviewRequest8, createdMember3.getMemberCode());

    // when
    List<ReviewRate> recommendBookList = bookRecommendService.getRecommendBookListByReviewRate();

    // then
    assertThat(recommendBookList).hasSize(5)
        .extracting("bookTitle", "reviewRate")
        .containsExactlyInAnyOrder(
            tuple("spring2", "5.0"),
            tuple("jpa", "4.5"),
            tuple("jpa2", "4.5"),
            tuple("docker", "3.5"),
            tuple("spring", "3.0")
        );
  }

  private static BookReviewServiceDto.Request createReviewRequest(String reviewTitle,
      String reviewContent, int reviewRate) {
    return BookReviewServiceDto.Request.builder()
        .reviewTitle(reviewTitle)
        .reviewContent(reviewContent)
        .reviewRate(reviewRate)
        .build();
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
}