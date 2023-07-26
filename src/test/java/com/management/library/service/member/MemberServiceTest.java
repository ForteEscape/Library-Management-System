package com.management.library.service.member;

import static com.management.library.domain.type.BookStatus.AVAILABLE;
import static com.management.library.domain.type.ExtendStatus.*;
import static com.management.library.domain.type.MemberRentalStatus.*;
import static com.management.library.domain.type.RentalStatus.OVERDUE;
import static com.management.library.domain.type.RentalStatus.PROCEEDING;
import static com.management.library.domain.type.RentalStatus.RETURNED;
import static com.management.library.exception.ErrorCode.*;
import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.domain.book.Book;
import com.management.library.domain.book.BookInfo;
import com.management.library.domain.member.Member;
import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import com.management.library.dto.ArrayResponseWrapper;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.service.member.dto.MemberCreateServiceDto.Request;
import com.management.library.service.member.dto.MemberCreateServiceDto.Response;
import com.management.library.service.member.dto.MemberReadServiceDto;
import com.management.library.service.rental.dto.RentalServiceReadDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
@Slf4j
class MemberServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private MemberService memberService;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BookRentalRepository bookRentalRepository;
  @Autowired
  private BookRepository bookRepository;

  @AfterEach
  void tearDown(){
    bookRentalRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();
  }

  @DisplayName("회원의 이름과 생일, 주소를 사용하여 회원 가입을 진행할 수 있다.")
  @Test
  public void createMember() throws Exception {
    // given
    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    Request request2 = createRequest("park", "980101", "경상남도", "김해시", "북부로");

    // when
    Response savedMember1 = memberService.createMember(request1);
    Response savedMember2 = memberService.createMember(request2);

    // then
    assertThat(List.of(savedMember1, savedMember2))
        .extracting("name", "birthdayCode", "legion", "city", "street", "memberCode", "password")
        .containsExactlyInAnyOrder(
            tuple("kim", "980101", "경상남도", "김해시", "삼계로", "100000000001", "980101!@#"),
            tuple("park", "980101", "경상남도", "김해시", "북부로", "100000000002", "980101!@#")
        );
  }

  @DisplayName("동일한 이름 및 주소를 가진 인원은 회원 가입이 제한된다.")
  @Test
  public void createMemberWithDuplicateInfo() throws Exception {
    // given
    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    Request request2 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");

    // when
    // then
    memberService.createMember(request1);
    assertThatThrownBy(() -> memberService.createMember(request2))
        .isInstanceOf(DuplicateException.class)
        .extracting("errorCode", "description")
        .contains(MEMBER_ALREADY_EXISTS, MEMBER_ALREADY_EXISTS.getDescription());

  }

  @DisplayName("이미 존재하는 회원 코드로 회원 가입을 진행할 수 없다.")
  @Test
  public void createMemberWithDuplicateMemberCode() throws Exception {
    // given
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    CountDownLatch latch = new CountDownLatch(2);

    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    Request request2 = createRequest("park", "980101", "경상남도", "김해시", "북부로");

    // when
    Future<Boolean> submit1 = executorService.submit(() -> {
      try {
        memberService.createMember(request1);
        return true;
      } catch (Exception e) {

        assertThat(e).isInstanceOf(DuplicateException.class)
            .extracting("errorCode", "description")
            .contains(DUPLICATE_MEMBER_CODE, DUPLICATE_MEMBER_CODE.getDescription());

        return false;
      } finally {
        latch.countDown();
      }
    });

    Future<Boolean> submit2 = executorService.submit(() -> {
      try {
        memberService.createMember(request2);
        return true;
      } catch (Exception e) {

        assertThat(e).isInstanceOf(DuplicateException.class)
            .extracting("errorCode", "description")
            .contains(DUPLICATE_MEMBER_CODE, DUPLICATE_MEMBER_CODE.getDescription());

        return false;
      } finally {
        latch.countDown();
      }
    });

    latch.await();
    assertThat(List.of(submit1.get(), submit2.get()))
        .contains(true, false);
  }

  @DisplayName("동시에 세 회원이 회원 가입을 진행하는 경우 한 명을 제외하고 회원 가입에 실패한다.")
  @Test
  public void createMemberWithConcurrentProblem() throws Exception {
    // given
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    CountDownLatch latch = new CountDownLatch(3);

    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    Request request2 = createRequest("park", "980101", "경상남도", "김해시", "북부로");
    Request request3 = createRequest("lee", "980101", "경상남도", "김해시", "해반천로");

    // when
    Future<Boolean> submit1 = executorService.submit(() -> {
      try {
        memberService.createMember(request1);
        return true;
      } catch (DuplicateException e) {
        return false;
      } finally {
        latch.countDown();
      }
    });

    Future<Boolean> submit2 = executorService.submit(() -> {
      try {
        memberService.createMember(request2);
        return true;
      } catch (DuplicateException e) {
        return false;
      } finally {
        latch.countDown();
      }
    });

    Future<Boolean> submit3 = executorService.submit(() -> {
      try {
        memberService.createMember(request3);
        return true;
      } catch (DuplicateException e) {
        return false;
      } finally {
        latch.countDown();
      }
    });
    latch.await();

    List<Boolean> result = List.of(submit1.get(), submit2.get(), submit3.get());

    // then
    assertThat(result)
        .hasSize(3)
        .contains(true, false, false);
  }

  @DisplayName("회원 번호로 회원의 정보를 조회할 수 있다.")
  @Test
  public void getMemberData() throws Exception {
    // given
    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    Request request2 = createRequest("park", "980102", "경상북도", "진주시", "북부로");
    Request request3 = createRequest("lee", "980103", "부산광역시", "동구", "해반천로");

    List<Request> list = List.of(request1, request2, request3);

    for (Request request : list) {
      memberService.createMember(request);
    }

    // when
    MemberReadServiceDto memberData = memberService.getMemberData("100000000002");

    // then
    assertThat(memberData)
        .extracting("name", "memberCode", "memberRentalStatus")
        .contains("park", "100000000002", RENTAL_AVAILABLE);
  }

  @DisplayName("존재하지 않는 회원 번호로 조회할 수 없다.")
  @Test
  public void getMemberDataWithNotExistsMemberCode() throws Exception {
    // given
    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    Request request2 = createRequest("park", "980102", "경상북도", "진주시", "북부로");
    Request request3 = createRequest("lee", "980103", "부산광역시", "동구", "해반천로");

    List<Request> list = List.of(request1, request2, request3);

    for (Request request : list) {
      memberService.createMember(request);
    }

    // when
    // then
    assertThatThrownBy(() -> memberService.getMemberData("100000000004"))
        .isInstanceOf(NoSuchElementExistsException.class)
        .extracting("errorCode", "description")
        .contains(MEMBER_NOT_EXISTS, MEMBER_NOT_EXISTS.getDescription());

  }

  @DisplayName("회원의 모든 대여 기록을 조회할 수 있다.")
  @Test
  public void getMemberRentalData() throws Exception {
    // given
    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    memberService.createMember(request1);

    Member member1 = memberRepository.findByMemberCode("100000000001")
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher", "location2", 2020, 130);

    bookRepository.saveAll(List.of(book1, book2, book3));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 21, 0, 0);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, ExtendStatus.AVAILABLE);
    Rental rental2 = createRental(book2, member1, RETURNED, rentalDate1, ExtendStatus.UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, PROCEEDING, rentalDate2, ExtendStatus.AVAILABLE);

    bookRentalRepository.saveAll(List.of(rental1, rental2, rental3));

    PageRequest pageRequest = PageRequest.of(0, 5);
    BookRentalSearchCond cond = new BookRentalSearchCond();
    // when
    ArrayResponseWrapper<Page<RentalServiceReadDto>> result = memberService.getMemberRentalData(
        cond, "100000000001", pageRequest);

    Long count = result.getCount();
    List<RentalServiceReadDto> content = result.getData().getContent();

    // then
    assertThat(count).isEqualTo(3);
    assertThat(content).hasSize(3)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa1", rentalDate1, rentalDate1.plusDays(14), ExtendStatus.AVAILABLE, RETURNED),
            tuple("spring", rentalDate1, rentalDate1.plusDays(14), UNAVAILABLE, RETURNED),
            tuple("jpa2", rentalDate2, rentalDate2.plusDays(14), ExtendStatus.AVAILABLE, PROCEEDING)
        );
  }

  @DisplayName("회원의 현재 대여 중인 대여 기록을 조회할 수 있다.")
  @Test
  public void getMemberRentalDataOnProceeding() throws Exception {
    // given
    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    memberService.createMember(request1);

    Member member1 = memberRepository.findByMemberCode("100000000001")
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher", "location2", 2020, 130);

    bookRepository.saveAll(List.of(book1, book2, book3));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 21, 0, 0);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, ExtendStatus.AVAILABLE);
    Rental rental2 = createRental(book2, member1, PROCEEDING, rentalDate2, ExtendStatus.UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, PROCEEDING, rentalDate2, ExtendStatus.AVAILABLE);

    bookRentalRepository.saveAll(List.of(rental1, rental2, rental3));

    PageRequest pageRequest = PageRequest.of(0, 5);
    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(PROCEEDING);

    // when
    ArrayResponseWrapper<Page<RentalServiceReadDto>> result = memberService.getMemberRentalData(
        cond, "100000000001", pageRequest);

    Long count = result.getCount();
    List<RentalServiceReadDto> content = result.getData().getContent();

    // then
    assertThat(count).isEqualTo(2);
    assertThat(content).hasSize(2)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("spring", rentalDate2, rentalDate2.plusDays(14), UNAVAILABLE, PROCEEDING),
            tuple("jpa2", rentalDate2, rentalDate2.plusDays(14), ExtendStatus.AVAILABLE, PROCEEDING)
        );
  }

  @DisplayName("회원의 반납된 대여 기록을 조회할 수 있다.")
  @Test
  public void getMemberRentalDataOnReturned() throws Exception {
    // given
    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    memberService.createMember(request1);

    Member member1 = memberRepository.findByMemberCode("100000000001")
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher", "location2", 2020, 130);

    bookRepository.saveAll(List.of(book1, book2, book3));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 21, 0, 0);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, ExtendStatus.AVAILABLE);
    Rental rental2 = createRental(book2, member1, RETURNED, rentalDate1, ExtendStatus.UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, PROCEEDING, rentalDate2, ExtendStatus.AVAILABLE);

    bookRentalRepository.saveAll(List.of(rental1, rental2, rental3));

    PageRequest pageRequest = PageRequest.of(0, 5);
    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(RETURNED);

    // when
    ArrayResponseWrapper<Page<RentalServiceReadDto>> result = memberService.getMemberRentalData(
        cond, "100000000001", pageRequest);

    Long count = result.getCount();
    List<RentalServiceReadDto> content = result.getData().getContent();

    // then
    assertThat(count).isEqualTo(2);
    assertThat(content).hasSize(2)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa1", rentalDate1, rentalDate1.plusDays(14), ExtendStatus.AVAILABLE, RETURNED),
            tuple("spring", rentalDate1, rentalDate1.plusDays(14), UNAVAILABLE, RETURNED)
        );
  }

  @DisplayName("회원의 연체 중인 대여 기록을 조회할 수 있다.")
  @Test
  public void getMemberRentalDataOnOverdue() throws Exception {
    // given
    Request request1 = createRequest("kim", "980101", "경상남도", "김해시", "삼계로");
    memberService.createMember(request1);

    Member member1 = memberRepository.findByMemberCode("100000000001")
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    Book book1 = createBook("jpa1", "kim", "publisher", "location", 2017, 130);
    Book book2 = createBook("spring", "park", "publisher1", "location1", 2017, 150);
    Book book3 = createBook("jpa2", "kim", "publisher", "location2", 2020, 130);

    bookRepository.saveAll(List.of(book1, book2, book3));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 21, 0, 0);

    Rental rental1 = createRental(book1, member1, OVERDUE, rentalDate1, ExtendStatus.AVAILABLE);
    Rental rental2 = createRental(book2, member1, OVERDUE, rentalDate1, ExtendStatus.UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, PROCEEDING, rentalDate2, ExtendStatus.AVAILABLE);

    bookRentalRepository.saveAll(List.of(rental1, rental2, rental3));

    PageRequest pageRequest = PageRequest.of(0, 5);
    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(OVERDUE);

    // when
    ArrayResponseWrapper<Page<RentalServiceReadDto>> result = memberService.getMemberRentalData(
        cond, "100000000001", pageRequest);

    Long count = result.getCount();
    List<RentalServiceReadDto> content = result.getData().getContent();

    // then
    assertThat(count).isEqualTo(2);
    assertThat(content).hasSize(2)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa1", rentalDate1, rentalDate1.plusDays(14), ExtendStatus.AVAILABLE, OVERDUE),
            tuple("spring", rentalDate1, rentalDate1.plusDays(14), UNAVAILABLE, OVERDUE)
        );
  }

  private static Request createRequest(String name, String birthdayCode, String legion, String city,
      String street) {
    return Request.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }

  private Book createBook(String title, String author, String publisher, String location,
      int publishedYear, int typeCode) {
    BookInfo bookInfo = createBookInfo(title, author, publisher, location, publishedYear);

    return Book.builder()
        .bookInfo(bookInfo)
        .bookStatus(AVAILABLE)
        .typeCode(typeCode)
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

  private static Rental createRental(Book book, Member member, RentalStatus rentalStatus,
      LocalDateTime rentalStartDate, ExtendStatus extendStatus) {
    return Rental.builder()
        .book(book)
        .member(member)
        .rentalStatus(rentalStatus)
        .rentalStartDate(rentalStartDate)
        .rentalEndDate(rentalStartDate.plusDays(14))
        .extendStatus(extendStatus)
        .build();
  }

}