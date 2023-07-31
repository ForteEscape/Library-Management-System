package com.management.library.service.rental;

import static com.management.library.domain.type.BookStatus.RENTAL;
import static com.management.library.domain.type.ExtendStatus.AVAILABLE;
import static com.management.library.domain.type.RentalStatus.OVERDUE;
import static com.management.library.domain.type.RentalStatus.PROCEEDING;
import static com.management.library.domain.type.RentalStatus.RETURNED;
import static com.management.library.exception.ErrorCode.BOOK_RENTAL_COUNT_EXCEED;
import static com.management.library.exception.ErrorCode.MEMBER_STATUS_NOT_AVAILABLE;
import static com.management.library.exception.ErrorCode.OVERDUE_RENTAL_EXISTS;
import static com.management.library.exception.ErrorCode.RENTAL_ALREADY_EXTEND;
import static com.management.library.exception.ErrorCode.RENTAL_STATUS_NOT_AVAILABLE;
import static com.management.library.exception.ErrorCode.UNABLE_TO_BOOK_RENTAL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.domain.book.Book;
import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.BookStatus;
import com.management.library.domain.type.ExtendStatus;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.exception.RentalException;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Request;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import com.management.library.service.rental.dto.RentalDurationExtendDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.management.library.service.rental.dto.ReturnBookResponseDto;
import java.time.LocalDate;
import java.util.ArrayList;
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
class RentalServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private BookRentalRepository bookRentalRepository;
  @Autowired
  private MemberService memberService;
  @Autowired
  private BookService bookService;
  @Autowired
  private RentalService rentalService;
  @Autowired
  private RedisTemplate<String, String> redisTemplate;
  @Autowired
  private RentalRedisService redisService;

  private static final String RENTAL_REDIS_KEY = "rental-count";
  private static final String PENALTY_MEMBER_KEY = "penalty:";
  private static final String BOOK_RENTED_COUNT = "book-rented-count";

  @AfterEach
  void tearDown() {
    bookRentalRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();

    redisTemplate.delete(RENTAL_REDIS_KEY);
    redisTemplate.delete(BOOK_RENTED_COUNT);

    for (int i = 1; i < 100; i++) {
      String keyCode = String.valueOf(100000000 + i);
      redisTemplate.delete(PENALTY_MEMBER_KEY + keyCode);
    }
  }

  @DisplayName("도서에 대한 대여를 등록할 수 있다.")
  @Test
  public void createBookRental() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Response createdBook = bookService.createNewBook(bookRequest1);

    RentalBookInfoDto bookInfo = createRentalData(createdBook);

    // when
    LocalDate rentedDate = LocalDate.now();
    RentalServiceResponseDto rental = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo, rentedDate);

    // then
    String memberRentalCount = String.valueOf(
        redisTemplate.opsForHash().get(RENTAL_REDIS_KEY, createdMember.getMemberCode()));

    Double score = redisTemplate.opsForZSet().score(BOOK_RENTED_COUNT, bookInfo.getBookTitle());

    Book book = bookRepository.findByTitleAndAuthor("jpa", "park").get();

    assertThat(score).isEqualTo(1);
    assertThat(book.getBookStatus()).isEqualTo(RENTAL);
    assertThat(memberRentalCount).isEqualTo("1");
    assertThat(rental)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .contains(
            "jpa", rentedDate, rentedDate.plusDays(14), AVAILABLE, PROCEEDING
        );
  }

  @DisplayName("3개 이상의 도서를 대여하려고 할 시 예외가 발생한다.")
  @Test
  public void createBookRentalTriple() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "lee", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest3 = createBookRequest("spring", "han", "publisher",
        2015, "location", 835);

    BookServiceCreateDto.Response createdBook1 = bookService.createNewBook(bookRequest1);
    BookServiceCreateDto.Response createdBook2 = bookService.createNewBook(bookRequest2);
    BookServiceCreateDto.Response createdBook3 = bookService.createNewBook(bookRequest3);

    RentalBookInfoDto bookInfo1 = createRentalData(createdBook1);
    RentalBookInfoDto bookInfo2 = createRentalData(createdBook2);
    RentalBookInfoDto bookInfo3 = createRentalData(createdBook3);

    LocalDate rentedDate = LocalDate.now();

    rentalService.createBookRental(createdMember.getMemberCode(), bookInfo1, rentedDate);
    rentalService.createBookRental(createdMember.getMemberCode(), bookInfo2, rentedDate);

    // when
    // then
    assertThatThrownBy(
        () -> rentalService.createBookRental(createdMember.getMemberCode(), bookInfo3, rentedDate))
        .isInstanceOf(RentalException.class)
        .extracting("errorCode", "description")
        .contains(
            BOOK_RENTAL_COUNT_EXCEED, BOOK_RENTAL_COUNT_EXCEED.getDescription()
        );
  }

  @DisplayName("대여 불가 상태에서 도서 대여를 할 수 없다.")
  @Test
  public void createBookRentalWithPenalty() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Response createdBook = bookService.createNewBook(bookRequest1);

    RentalBookInfoDto bookInfo = createRentalData(createdBook);

    redisTemplate.opsForValue()
        .set(PENALTY_MEMBER_KEY + createdMember.getMemberCode(), "2023-07-31");
    LocalDate rentedDate = LocalDate.now();

    // when
    // then
    assertThatThrownBy(
        () -> rentalService.createBookRental(createdMember.getMemberCode(), bookInfo, rentedDate))
        .isInstanceOf(RentalException.class)
        .extracting("errorCode", "description")
        .contains(
            UNABLE_TO_BOOK_RENTAL, UNABLE_TO_BOOK_RENTAL.getDescription()
        );
  }

  @DisplayName("연체 상태인 대여가 존재할 경우 대여를 할 수 없다.")
  @Test
  public void createBookRentalWithOverdueRental() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Response createdBook1 = bookService.createNewBook(bookRequest1);
    BookServiceCreateDto.Response createdBook2 = bookService.createNewBook(bookRequest2);

    RentalBookInfoDto bookInfo1 = createRentalData(createdBook1);
    RentalBookInfoDto bookInfo2 = createRentalData(createdBook2);

    LocalDate rentedDate = LocalDate.now();
    RentalServiceResponseDto rental = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo1, rentedDate);

    Rental rental1 = bookRentalRepository.findById(rental.getId()).get();
    rental1.changeRentalStatus(OVERDUE);

    // when
    // then
    assertThatThrownBy(
        () -> rentalService.createBookRental(createdMember.getMemberCode(), bookInfo2, rentedDate))
        .isInstanceOf(RentalException.class)
        .extracting("errorCode", "description")
        .contains(
            OVERDUE_RENTAL_EXISTS, OVERDUE_RENTAL_EXISTS.getDescription()
        );
  }

  @DisplayName("도서 대여 기간을 연장할 수 있다.")
  @Test
  public void extendRentalDuration() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Response createdBook = bookService.createNewBook(bookRequest1);

    RentalBookInfoDto bookInfo = createRentalData(createdBook);
    LocalDate rentedDate = LocalDate.now();

    RentalServiceResponseDto rental = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo, rentedDate);

    // when
    RentalDurationExtendDto extendResult = rentalService.extendRentalDuration(
        createdMember.getMemberCode(), rental.getId());

    // then
    assertThat(extendResult)
        .extracting("rentalStartDate", "rentalEndDate")
        .contains(
            rentedDate, rentedDate.plusDays(14).plusDays(7)
        );
  }

  @DisplayName("회원이 대여 불가능한 상태인 경우 연장이 불가능하다.")
  @Test
  public void extendRentalDurationWhenMemberRentalUnavailable() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Response createdBook = bookService.createNewBook(bookRequest1);

    RentalBookInfoDto bookInfo = createRentalData(createdBook);
    LocalDate rentedDate = LocalDate.now();

    RentalServiceResponseDto rental = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo, rentedDate);

    redisTemplate.opsForValue()
        .set(PENALTY_MEMBER_KEY + createdMember.getMemberCode(), "2023-07-31");

    // when
    // then
    assertThatThrownBy(
        () -> rentalService.extendRentalDuration(createdMember.getMemberCode(), rental.getId()))
        .isInstanceOf(RentalException.class)
        .extracting("errorCode", "description")
        .contains(
            MEMBER_STATUS_NOT_AVAILABLE, MEMBER_STATUS_NOT_AVAILABLE.getDescription()
        );
  }

  @DisplayName("해당 대여가 연체된 대여인 경우 연장할 수 없다.")
  @Test
  public void extendRentalDurationWithOverdue() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Response createdBook = bookService.createNewBook(bookRequest1);

    RentalBookInfoDto bookInfo = createRentalData(createdBook);
    LocalDate rentedDate = LocalDate.now();

    RentalServiceResponseDto rental = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo, rentedDate);

    Rental rental1 = bookRentalRepository.findById(rental.getId()).get();
    rental1.changeRentalStatus(OVERDUE);

    // when
    // then
    assertThatThrownBy(
        () -> rentalService.extendRentalDuration(createdMember.getMemberCode(), rental.getId()))
        .isInstanceOf(RentalException.class)
        .extracting("errorCode", "description")
        .contains(
            RENTAL_STATUS_NOT_AVAILABLE, RENTAL_STATUS_NOT_AVAILABLE.getDescription()
        );
  }

  @DisplayName("해당 대여가 이미 연장된 대여인 경우 연장할 수 없다.")
  @Test
  public void extendRentalDurationWithAlreadyExtend() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Response createdBook = bookService.createNewBook(bookRequest1);

    RentalBookInfoDto bookInfo = createRentalData(createdBook);
    LocalDate rentedDate = LocalDate.now();

    RentalServiceResponseDto rental = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo, rentedDate);

    rentalService.extendRentalDuration(createdMember.getMemberCode(), rental.getId());

    // when
    // then
    assertThatThrownBy(
        () -> rentalService.extendRentalDuration(createdMember.getMemberCode(), rental.getId()))
        .isInstanceOf(RentalException.class)
        .extracting("errorCode", "description")
        .contains(
            RENTAL_ALREADY_EXTEND, RENTAL_ALREADY_EXTEND.getDescription()
        );
  }

  @DisplayName("이미 연체된 대여가 존재할 경우 연장할 수 없다.")
  @Test
  public void extendRentalDurationOverdueRentalExists() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "park", "publisher", 2015,
        "location", 835);

    BookServiceCreateDto.Response createdBook1 = bookService.createNewBook(bookRequest1);
    BookServiceCreateDto.Response createdBook2 = bookService.createNewBook(bookRequest2);

    RentalBookInfoDto bookInfo1 = createRentalData(createdBook1);
    RentalBookInfoDto bookInfo2 = createRentalData(createdBook2);
    LocalDate rentedDate = LocalDate.now();

    RentalServiceResponseDto rental1 = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo1, rentedDate);
    RentalServiceResponseDto rental2 = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo2, rentedDate);

    Rental rental = bookRentalRepository.findById(rental1.getId()).get();
    rental.changeRentalStatus(OVERDUE);

    // when
    // then
    assertThatThrownBy(() -> rentalService.extendRentalDuration(createdMember.getMemberCode(),
        rental2.getId()))
        .isInstanceOf(RentalException.class)
        .extracting("errorCode", "description")
        .contains(
            OVERDUE_RENTAL_EXISTS, OVERDUE_RENTAL_EXISTS.getDescription()
        );
  }

  @DisplayName("대여한 도서의 반납을 수행할 수 있다.")
  @Test
  public void returnBook() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Response createdBook = bookService.createNewBook(bookRequest1);

    RentalBookInfoDto bookInfo = createRentalData(createdBook);
    LocalDate rentedDate = LocalDate.now();

    RentalServiceResponseDto rental = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo, rentedDate);

    // when
    ReturnBookResponseDto returnResult = rentalService.returnBook(
        createdMember.getMemberCode(), bookInfo.getBookTitle(), bookInfo.getAuthor());

    // then
    Book book = bookRepository.findByTitleAndAuthor("jpa", "park").get();
    String memberRentalCount = String.valueOf(
        redisTemplate.opsForHash().get(RENTAL_REDIS_KEY, createdMember.getMemberCode()));

    assertThat(book.getBookStatus()).isEqualTo(BookStatus.AVAILABLE);
    assertThat(memberRentalCount).isEqualTo("2");
    assertThat(returnResult)
        .extracting("bookTitle", "author", "rentalStatus", "overdueDate")
        .contains(
            "jpa", "park", RETURNED, "NOT-OVERDUE"
        );
  }

  @DisplayName("연체된 도서를 반납할 경우, 특정 날짜까지 대여를 할 수 없다.")
  @Test
  public void returnBookWithOverdue() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Response createdBook = bookService.createNewBook(bookRequest1);

    RentalBookInfoDto bookInfo = createRentalData(createdBook);
    LocalDate rentedDate = LocalDate.now().minusDays(19);

    RentalServiceResponseDto rental = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo, rentedDate);

    Rental rental1 = bookRentalRepository.findById(rental.getId()).get();
    rental1.changeRentalStatus(OVERDUE);

    // when
    ReturnBookResponseDto returnResult = rentalService.returnBook(
        createdMember.getMemberCode(), bookInfo.getBookTitle(), bookInfo.getAuthor());

    // then
    Book book = bookRepository.findByTitleAndAuthor("jpa", "park").get();
    String memberRentalCount = String.valueOf(
        redisTemplate.opsForHash().get(RENTAL_REDIS_KEY, createdMember.getMemberCode()));

    boolean penalty = redisService.checkMemberRentalPenalty(createdMember.getMemberCode());

    assertThat(penalty).isTrue();
    assertThat(book.getBookStatus()).isEqualTo(BookStatus.AVAILABLE);
    assertThat(memberRentalCount).isEqualTo("2");
    assertThat(returnResult)
        .extracting("bookTitle", "author", "rentalStatus", "overdueDate")
        .contains(
            "jpa", "park", OVERDUE, LocalDate.now().plusDays(5).toString()
        );
  }

  @DisplayName("이미 연체 패널티를 가지고 있는 상태에서 연체된 도서를 반납할 경우 추가적인 패널티가 부가된다.")
  @Test
  public void returnBookWithOverdueInPenalty() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Response createdMember = memberService.createMember(memberRequest1);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "park", "publisher", 2015,
        "location", 835);

    BookServiceCreateDto.Response createdBook1 = bookService.createNewBook(bookRequest1);
    BookServiceCreateDto.Response createdBook2 = bookService.createNewBook(bookRequest2);

    RentalBookInfoDto bookInfo1 = createRentalData(createdBook1);
    RentalBookInfoDto bookInfo2 = createRentalData(createdBook2);

    LocalDate rentedDate1 = LocalDate.now().minusDays(19);
    LocalDate rentedDate2 = LocalDate.now().minusDays(16);

    RentalServiceResponseDto rental1 = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo1, rentedDate1);
    RentalServiceResponseDto rental2 = rentalService.createBookRental(
        createdMember.getMemberCode(), bookInfo2, rentedDate2);

    Rental rentalResult1 = bookRentalRepository.findById(rental1.getId()).get();
    Rental rentalResult2 = bookRentalRepository.findById(rental2.getId()).get();
    rentalResult1.changeRentalStatus(OVERDUE);
    rentalResult2.changeRentalStatus(OVERDUE);

    // when
    rentalService.returnBook(createdMember.getMemberCode(), bookInfo1.getBookTitle(),
        bookInfo1.getAuthor());

    ReturnBookResponseDto returnResult = rentalService.returnBook(
        createdMember.getMemberCode(), bookInfo2.getBookTitle(), bookInfo2.getAuthor());

    // then
    Book book1 = bookRepository.findByTitleAndAuthor("jpa", "park").get();
    Book book2 = bookRepository.findByTitleAndAuthor("jpa2", "park").get();
    String memberRentalCount = String.valueOf(
        redisTemplate.opsForHash().get(RENTAL_REDIS_KEY, createdMember.getMemberCode()));

    boolean penalty = redisService.checkMemberRentalPenalty(createdMember.getMemberCode());

    assertThat(penalty).isTrue();
    assertThat(book1.getBookStatus()).isEqualTo(BookStatus.AVAILABLE);
    assertThat(book2.getBookStatus()).isEqualTo(BookStatus.AVAILABLE);
    assertThat(memberRentalCount).isEqualTo("2");
    assertThat(returnResult)
        .extracting("bookTitle", "author", "rentalStatus", "overdueDate")
        .contains(
            "jpa2", "park", OVERDUE, LocalDate.now().plusDays(7).toString()
        );
  }

  @DisplayName("모든 대여 기록을 조회할 수 있다.")
  @Test
  public void getRentalData() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("park", "980101", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response createdMember1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response createdMember2 = memberService.createMember(memberRequest2);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest3 = createBookRequest("spring", "lee", "publisher2",
        2015, "location2", 835);
    BookServiceCreateDto.Request bookRequest4 = createBookRequest("spring2", "lee", "publisher2",
        2015, "location2", 835);

    List<Request> bookRequests = List.of(bookRequest1, bookRequest2, bookRequest3, bookRequest4);
    List<RentalBookInfoDto> rentalDataList = new ArrayList<>();

    for (Request bookRequest : bookRequests) {
      Response newBook = bookService.createNewBook(bookRequest);
      rentalDataList.add(createRentalData(newBook));
    }

    LocalDate rentalDate1 = LocalDate.now().minusDays(5);
    LocalDate rentalDate2 = LocalDate.now().minusDays(8);
    LocalDate rentalDate3 = LocalDate.now().minusDays(11);

    RentalServiceResponseDto bookRental1 = rentalService.createBookRental(
        createdMember1.getMemberCode(), rentalDataList.get(0), rentalDate1);

    RentalServiceResponseDto bookRental2 = rentalService.createBookRental(
        createdMember1.getMemberCode(), rentalDataList.get(1), rentalDate2);

    RentalServiceResponseDto bookRental3 = rentalService.createBookRental(
        createdMember2.getMemberCode(), rentalDataList.get(2), rentalDate2);

    RentalServiceResponseDto bookRental4 = rentalService.createBookRental(
        createdMember2.getMemberCode(), rentalDataList.get(3), rentalDate3);

    BookRentalSearchCond cond = new BookRentalSearchCond();
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<RentalServiceResponseDto> rentalData = rentalService.getRentalData(cond, pageRequest);
    List<RentalServiceResponseDto> content = rentalData.getContent();

    // then
    assertThat(content).hasSize(4)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", rentalDate1, rentalDate1.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("jpa2", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("spring", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("spring2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );
  }

  @DisplayName("반납 처리된 대여 기록을 조회할 수 있다.")
  @Test
  public void getRentalDataWithReturned() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("park", "980101", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response createdMember1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response createdMember2 = memberService.createMember(memberRequest2);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest3 = createBookRequest("spring", "lee", "publisher2",
        2015, "location2", 835);
    BookServiceCreateDto.Request bookRequest4 = createBookRequest("spring2", "lee", "publisher2",
        2015, "location2", 835);

    List<Request> bookRequests = List.of(bookRequest1, bookRequest2, bookRequest3, bookRequest4);
    List<RentalBookInfoDto> rentalDataList = new ArrayList<>();

    for (Request bookRequest : bookRequests) {
      Response newBook = bookService.createNewBook(bookRequest);
      rentalDataList.add(createRentalData(newBook));
    }

    LocalDate rentalDate1 = LocalDate.now().minusDays(5);
    LocalDate rentalDate2 = LocalDate.now().minusDays(8);
    LocalDate rentalDate3 = LocalDate.now().minusDays(11);

    RentalServiceResponseDto bookRental1 = rentalService.createBookRental(
        createdMember1.getMemberCode(), rentalDataList.get(0), rentalDate1);

    RentalServiceResponseDto bookRental2 = rentalService.createBookRental(
        createdMember1.getMemberCode(), rentalDataList.get(1), rentalDate2);

    RentalServiceResponseDto bookRental3 = rentalService.createBookRental(
        createdMember2.getMemberCode(), rentalDataList.get(2), rentalDate2);

    RentalServiceResponseDto bookRental4 = rentalService.createBookRental(
        createdMember2.getMemberCode(), rentalDataList.get(3), rentalDate3);

    Rental rental1 = bookRentalRepository.findById(bookRental1.getId()).get();
    Rental rental2 = bookRentalRepository.findById(bookRental4.getId()).get();
    rental1.changeRentalStatus(RETURNED);
    rental2.changeRentalStatus(RETURNED);

    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(RETURNED);
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<RentalServiceResponseDto> rentalData = rentalService.getRentalData(cond, pageRequest);
    List<RentalServiceResponseDto> content = rentalData.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", rentalDate1, rentalDate1.plusDays(14), AVAILABLE, RETURNED),
            tuple("spring2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, RETURNED)
        );
  }

  @DisplayName("연체된 대여 기록을 조회할 수 있다.")
  @Test
  public void getRentalDataWithOverdue() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest1 = createMemberRequest("kim", "980101", "경상남도",
        "김해시", "삼계로");
    MemberCreateServiceDto.Request memberRequest2 = createMemberRequest("park", "980101", "경상남도",
        "김해시", "삼계로");

    MemberCreateServiceDto.Response createdMember1 = memberService.createMember(memberRequest1);
    MemberCreateServiceDto.Response createdMember2 = memberService.createMember(memberRequest2);

    BookServiceCreateDto.Request bookRequest1 = createBookRequest("jpa", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest2 = createBookRequest("jpa2", "park", "publisher", 2015,
        "location", 835);
    BookServiceCreateDto.Request bookRequest3 = createBookRequest("spring", "lee", "publisher2",
        2015, "location2", 835);
    BookServiceCreateDto.Request bookRequest4 = createBookRequest("spring2", "lee", "publisher2",
        2015, "location2", 835);

    List<Request> bookRequests = List.of(bookRequest1, bookRequest2, bookRequest3, bookRequest4);
    List<RentalBookInfoDto> rentalDataList = new ArrayList<>();

    for (Request bookRequest : bookRequests) {
      Response newBook = bookService.createNewBook(bookRequest);
      rentalDataList.add(createRentalData(newBook));
    }

    LocalDate rentalDate1 = LocalDate.now().minusDays(5);
    LocalDate rentalDate2 = LocalDate.now().minusDays(8);
    LocalDate rentalDate3 = LocalDate.now().minusDays(11);

    RentalServiceResponseDto bookRental1 = rentalService.createBookRental(
        createdMember1.getMemberCode(), rentalDataList.get(0), rentalDate1);

    RentalServiceResponseDto bookRental2 = rentalService.createBookRental(
        createdMember1.getMemberCode(), rentalDataList.get(1), rentalDate2);

    RentalServiceResponseDto bookRental3 = rentalService.createBookRental(
        createdMember2.getMemberCode(), rentalDataList.get(2), rentalDate2);

    RentalServiceResponseDto bookRental4 = rentalService.createBookRental(
        createdMember2.getMemberCode(), rentalDataList.get(3), rentalDate3);

    Rental rental1 = bookRentalRepository.findById(bookRental2.getId()).get();
    Rental rental2 = bookRentalRepository.findById(bookRental3.getId()).get();
    rental1.changeRentalStatus(OVERDUE);
    rental2.changeRentalStatus(OVERDUE);

    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(OVERDUE);
    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<RentalServiceResponseDto> rentalData = rentalService.getRentalData(cond, pageRequest);
    List<RentalServiceResponseDto> content = rentalData.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa2", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, OVERDUE),
            tuple("spring", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, OVERDUE)
        );
  }

  @DisplayName("회원의 모든 대여 기록을 조회할 수 있다.")
  @Test
  public void getMemberRentalData() throws Exception {
    // given
    MemberCreateServiceDto.Request request1 = createMemberRequest("kim", "980101", "경상남도", "김해시",
        "삼계로");
    MemberCreateServiceDto.Response member = memberService.createMember(request1);

    Request bookRequest1 = createBookRequest("jpa1", "kim", "publisher", 2017, "location", 130);
    Request bookRequest2 = createBookRequest("spring", "park", "publisher1", 2017, "location1",
        150);
    Request bookRequest3 = createBookRequest("jpa2", "kim", "publisher", 2020, "location2", 130);

    Response newBook1 = bookService.createNewBook(bookRequest1);
    Response newBook2 = bookService.createNewBook(bookRequest2);
    Response newBook3 = bookService.createNewBook(bookRequest3);

    LocalDate rentedDate = LocalDate.now();

    RentalBookInfoDto rentalData1 = createRentalData(newBook1);
    RentalBookInfoDto rentalData2 = createRentalData(newBook2);
    RentalBookInfoDto rentalData3 = createRentalData(newBook3);

    rentalService.createBookRental(member.getMemberCode(), rentalData1, rentedDate);
    rentalService.createBookRental(member.getMemberCode(), rentalData2, rentedDate);

    rentalService.returnBook(member.getMemberCode(), rentalData2.getBookTitle(), rentalData2.getAuthor());

    rentalService.createBookRental(member.getMemberCode(), rentalData3, rentedDate);

    PageRequest pageRequest = PageRequest.of(0, 5);
    BookRentalSearchCond cond = new BookRentalSearchCond();

    // when
    Page<RentalServiceResponseDto> result = rentalService.getMemberRentalData(cond,
        member.getMemberCode(), pageRequest);

    List<RentalServiceResponseDto> content = result.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa1", rentedDate, rentedDate.plusDays(14), ExtendStatus.AVAILABLE, PROCEEDING),
            tuple("spring", rentedDate, rentedDate.plusDays(14), AVAILABLE, RETURNED),
            tuple("jpa2", rentedDate, rentedDate.plusDays(14), ExtendStatus.AVAILABLE, PROCEEDING)
        );
  }

  @DisplayName("회원의 현재 대여 중인 대여 기록을 조회할 수 있다.")
  @Test
  public void getMemberRentalDataOnProceeding() throws Exception {
    // given
    MemberCreateServiceDto.Request request1 = createMemberRequest("kim", "980101", "경상남도", "김해시",
        "삼계로");
    MemberCreateServiceDto.Response member = memberService.createMember(request1);

    Request bookRequest1 = createBookRequest("jpa1", "kim", "publisher", 2017, "location", 130);
    Request bookRequest2 = createBookRequest("spring", "park", "publisher1", 2017, "location1",
        150);
    Request bookRequest3 = createBookRequest("jpa2", "kim", "publisher", 2020, "location2", 130);

    Response newBook1 = bookService.createNewBook(bookRequest1);
    Response newBook2 = bookService.createNewBook(bookRequest2);
    Response newBook3 = bookService.createNewBook(bookRequest3);

    LocalDate rentedDate = LocalDate.now();

    RentalBookInfoDto rentalData1 = createRentalData(newBook1);
    RentalBookInfoDto rentalData2 = createRentalData(newBook2);
    RentalBookInfoDto rentalData3 = createRentalData(newBook3);

    rentalService.createBookRental(member.getMemberCode(), rentalData1, rentedDate);
    rentalService.createBookRental(member.getMemberCode(), rentalData2, rentedDate);

    rentalService.returnBook(member.getMemberCode(), rentalData2.getBookTitle(), rentalData2.getAuthor());

    rentalService.createBookRental(member.getMemberCode(), rentalData3, rentedDate);

    PageRequest pageRequest = PageRequest.of(0, 5);
    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(PROCEEDING);

    // when
    Page<RentalServiceResponseDto> result = rentalService.getMemberRentalData(cond,
        member.getMemberCode(), pageRequest);

    List<RentalServiceResponseDto> content = result.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa1", rentedDate, rentedDate.plusDays(14), ExtendStatus.AVAILABLE, PROCEEDING),
            tuple("jpa2", rentedDate, rentedDate.plusDays(14), ExtendStatus.AVAILABLE, PROCEEDING)
        );
  }

  @DisplayName("회원의 반납된 대여 기록을 조회할 수 있다.")
  @Test
  public void getMemberRentalDataOnReturned() throws Exception {
    // given
    MemberCreateServiceDto.Request request1 = createMemberRequest("kim", "980101", "경상남도", "김해시",
        "삼계로");
    MemberCreateServiceDto.Response member = memberService.createMember(request1);

    Request bookRequest1 = createBookRequest("jpa1", "kim", "publisher", 2017, "location", 130);
    Request bookRequest2 = createBookRequest("spring", "park", "publisher1", 2017, "location1",
        150);
    Request bookRequest3 = createBookRequest("jpa2", "kim", "publisher", 2020, "location2", 130);

    Response newBook1 = bookService.createNewBook(bookRequest1);
    Response newBook2 = bookService.createNewBook(bookRequest2);
    Response newBook3 = bookService.createNewBook(bookRequest3);

    LocalDate rentedDate = LocalDate.now();

    RentalBookInfoDto rentalData1 = createRentalData(newBook1);
    RentalBookInfoDto rentalData2 = createRentalData(newBook2);
    RentalBookInfoDto rentalData3 = createRentalData(newBook3);

    rentalService.createBookRental(member.getMemberCode(), rentalData1, rentedDate);
    rentalService.createBookRental(member.getMemberCode(), rentalData2, rentedDate);

    rentalService.returnBook(member.getMemberCode(), rentalData2.getBookTitle(), rentalData2.getAuthor());

    rentalService.createBookRental(member.getMemberCode(), rentalData3, rentedDate);

    PageRequest pageRequest = PageRequest.of(0, 5);
    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(RETURNED);

    // when
    Page<RentalServiceResponseDto> result = rentalService.getMemberRentalData(cond,
        member.getMemberCode(), pageRequest);

    List<RentalServiceResponseDto> content = result.getContent();

    // then
    assertThat(content).hasSize(1)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("spring", rentedDate, rentedDate.plusDays(14), AVAILABLE, RETURNED)
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

}