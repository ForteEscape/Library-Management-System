package com.management.library.service.query;

import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.management.ManagementRequestRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.newbook.NewBookRequestRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Request;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.query.dto.MemberTotalInfoDto;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import com.management.library.service.request.RedisRequestService;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberTotalInfoServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private MemberService memberService;
  @Autowired
  private ManagementService managementService;
  @Autowired
  private NewBookService newBookService;
  @Autowired
  private BookService bookService;
  @Autowired
  private RentalService rentalService;
  @Autowired
  private RedisRequestService redisRequestService;
  @Autowired
  private MemberTotalInfoService memberTotalInfoService;

  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private ManagementRequestRepository managementRequestRepository;
  @Autowired
  private NewBookRequestRepository newBookRequestRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private BookRentalRepository bookRentalRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private static final String MANAGEMENT_REQUEST_PREFIX = "management-request-id:";
  private static final String MANAGEMENT_REQUEST_CACHE_KEY = "management-request-count:";
  private static final String NEW_BOOK_REQUEST_PREFIX = "book-request-id:";
  private static final String NEW_BOOK_CACHE_KEY = "book-request-count:";
  private static final String RENTAL_REDIS_KEY = "rental-count";
  private static final String PENALTY_MEMBER_KEY = "penalty:";
  private static final String BOOK_RENTED_COUNT = "book-rented-count";

  @AfterEach
  void tearDown() {
    bookRentalRepository.deleteAllInBatch();
    managementRequestRepository.deleteAllInBatch();
    newBookRequestRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();

    redisRequestService.deleteCache(NEW_BOOK_CACHE_KEY);
    redisRequestService.deleteCache(MANAGEMENT_REQUEST_CACHE_KEY);
    redisTemplate.delete(RENTAL_REDIS_KEY);
    redisTemplate.delete(BOOK_RENTED_COUNT);

    for (int i = 1; i < 100; i++) {
      String keyCode = String.valueOf(100000000 + i);
      redisRequestService.deleteCache(NEW_BOOK_REQUEST_PREFIX + i);
      redisRequestService.deleteCache(MANAGEMENT_REQUEST_PREFIX + i);
      redisTemplate.delete(PENALTY_MEMBER_KEY + keyCode);
    }
  }

  @DisplayName("회원의 정보를 통합하여 가져올 수 있다.")
  @Test
  public void getMemberTotalInfo() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberCreateServiceDto.Response savedMember = memberService.createMember(memberRequest);

    // when
    MemberTotalInfoDto memberData = memberTotalInfoService.getMemberTotalInfo(
        savedMember.getMemberCode());

    // then
    assertThat(memberData)
        .extracting("name", "birthdayCode", "remainManagementRequestCount",
            "remainNewBookRequestCount", "remainRentalCount", "rentalStatus")
        .contains(
            "kim", "980101", "5", "5", "2", "available"
        );
  }

  @DisplayName("요구 상황을 등록한 경우 남은 요구 상황 등록 가능 개수를 정확히 가져올 수 있다.")
  @Test
  public void getMemberTotalInfoWithCorrectData() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberCreateServiceDto.Response savedMember = memberService.createMember(memberRequest);

    ManagementRequestServiceDto.Request managementCreateRequest = createManagementRequest("title1",
        "content1");
    managementService.createManagementRequest(managementCreateRequest, savedMember.getMemberCode());

    NewBookRequestServiceDto.Request newBookRequestForm = createNewBookRequest("book1", "content1");
    newBookService.createNewBookRequest(newBookRequestForm, savedMember.getMemberCode());

    // when
    MemberTotalInfoDto memberData = memberTotalInfoService.getMemberTotalInfo(
        savedMember.getMemberCode());

    // then
    assertThat(memberData)
        .extracting("name", "birthdayCode", "remainManagementRequestCount",
            "remainNewBookRequestCount", "remainRentalCount", "rentalStatus")
        .contains(
            "kim", "980101", "4", "4", "2", "available"
        );
  }

  @DisplayName("요구 상황과 대여를 등록한 경우 남은 요구 상황 등록 가능 개수와 대여 가능 횟수를 정확히 가져올 수 있다.")
  @Test
  public void getMemberTotalInfoWithRentalData() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberCreateServiceDto.Response savedMember = memberService.createMember(memberRequest);

    ManagementRequestServiceDto.Request managementCreateRequest = createManagementRequest("title1",
        "content1");
    managementService.createManagementRequest(managementCreateRequest, savedMember.getMemberCode());

    NewBookRequestServiceDto.Request newBookRequestForm = createNewBookRequest("book1", "content1");
    newBookService.createNewBookRequest(newBookRequestForm, savedMember.getMemberCode());

    Request bookRequest = createBookRequest("book1", "park", "publisher", 2015, "location", 130);
    Response newBook = bookService.createNewBook(bookRequest);

    RentalBookInfoDto rentalData = createRentalData(newBook);
    rentalService.createBookRental(savedMember.getMemberCode(), rentalData, LocalDate.now());

    // when
    MemberTotalInfoDto memberData = memberTotalInfoService.getMemberTotalInfo(
        savedMember.getMemberCode());

    // then
    assertThat(memberData)
        .extracting("name", "birthdayCode", "remainManagementRequestCount",
            "remainNewBookRequestCount", "remainRentalCount", "rentalStatus")
        .contains(
            "kim", "980101", "4", "4", "1", "available"
        );
  }

  @DisplayName("대여 가능 횟수가 0일시 더 이상 대여가 불가능하다는 상태로 변해야 한다.")
  @Test
  public void getMemberTotalInfoWithRentalCountZero() throws Exception {
    // given
    MemberCreateServiceDto.Request memberRequest = createMemberRequest("kim", "980101", "경남", "김해",
        "삼계로");
    MemberCreateServiceDto.Response savedMember = memberService.createMember(memberRequest);

    ManagementRequestServiceDto.Request managementCreateRequest = createManagementRequest("title1",
        "content1");
    managementService.createManagementRequest(managementCreateRequest, savedMember.getMemberCode());

    NewBookRequestServiceDto.Request newBookRequestForm = createNewBookRequest("book1", "content1");
    newBookService.createNewBookRequest(newBookRequestForm, savedMember.getMemberCode());

    Request bookRequest1 = createBookRequest("book1", "park", "publisher", 2015, "location", 130);
    Request bookRequest2 = createBookRequest("book2", "lee", "publisher", 2015, "location", 135);
    Response newBook1 = bookService.createNewBook(bookRequest1);
    Response newBook2 = bookService.createNewBook(bookRequest2);

    RentalBookInfoDto rentalData1 = createRentalData(newBook1);
    RentalBookInfoDto rentalData2 = createRentalData(newBook2);
    rentalService.createBookRental(savedMember.getMemberCode(), rentalData1, LocalDate.now());
    rentalService.createBookRental(savedMember.getMemberCode(), rentalData2, LocalDate.now());

    // when
    MemberTotalInfoDto memberData = memberTotalInfoService.getMemberTotalInfo(
        savedMember.getMemberCode());

    // then
    assertThat(memberData)
        .extracting("name", "birthdayCode", "remainManagementRequestCount",
            "remainNewBookRequestCount", "remainRentalCount", "rentalStatus")
        .contains(
            "kim", "980101", "4", "4", "0", "unavailable"
        );
  }

  private MemberCreateServiceDto.Request createMemberRequest(String name, String birthdayCode,
      String legion, String city, String street) {
    return MemberCreateServiceDto.Request.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }

  private NewBookRequestServiceDto.Request createNewBookRequest(String title,
      String content) {
    return NewBookRequestServiceDto.Request.builder()
        .requestBookTitle(title)
        .requestContent(content)
        .build();
  }

  private ManagementRequestServiceDto.Request createManagementRequest(String title,
      String content) {
    return ManagementRequestServiceDto.Request.builder()
        .title(title)
        .content(content)
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

  private RentalBookInfoDto createRentalData(Response createdBook) {
    return RentalBookInfoDto.builder()
        .bookTitle(createdBook.getTitle())
        .author(createdBook.getAuthor())
        .build();
  }
}