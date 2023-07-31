package com.management.library.service.review;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.repository.review.BookReviewRepository;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.RedisMemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.member.dto.MemberCreateServiceDto.Request;
import com.management.library.service.rental.RentalRedisService;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.management.library.service.review.dto.BookReviewServiceDto;
import java.time.LocalDate;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
  private RedisMemberService redisMemberService;
  @Autowired
  private RentalRedisService rentalRedisService;

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

  @AfterEach
  void tearDown(){
    bookReviewRepository.deleteAllInBatch();
    bookRentalRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();

    redisTemplate.delete("memberCode");
    redisTemplate.delete(RENTAL_REDIS_KEY);
    redisTemplate.delete(BOOK_RENTED_COUNT);
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

    rentalService.returnBook(member.getMemberCode(), bookRequest.getTitle(), bookRequest.getAuthor());

    BookReviewServiceDto.Request reviewRequest = createReviewRequest("review", "reviewContent", 5);


    // when
    BookReviewServiceDto.Response review = bookReviewService.createReview(bookRental.getId(),
        reviewRequest, member.getMemberCode());

    // then
    Assertions.assertThat(review)
        .extracting("reviewTitle", "reviewContent", "reviewRate")
        .contains(
            "review", "reviewContent", 5
        );
  }

  private static BookReviewServiceDto.Request createReviewRequest(String reviewTitle, String reviewContent, int reviewRate) {
    return BookReviewServiceDto.Request.builder()
        .reviewTitle(reviewTitle)
        .reviewContent(reviewContent)
        .reviewRate(reviewRate)
        .build();
  }

  @DisplayName("리뷰할 도서에 대한 반납 기록이 없다면 리뷰를 등록할 수 없다.")
  @Test
  public void createReviewWithoutRental() throws Exception {
    // given

    // when

    // then
  }

  @DisplayName("하나의 도서를 2번 리뷰할 수 없다.")
  @Test
  public void createReviewWithTwice() throws Exception {
    // given

    // when

    // then
  }

  @DisplayName("리뷰의 재목과 내용을 변경할 수 있다.")
  @Test
  public void updateReview() throws Exception {
    // given

    // when

    // then
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