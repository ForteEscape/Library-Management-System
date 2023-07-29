package com.management.library.repository.rental;

import static com.management.library.domain.type.ExtendStatus.AVAILABLE;
import static com.management.library.domain.type.ExtendStatus.UNAVAILABLE;
import static com.management.library.domain.type.RentalStatus.OVERDUE;
import static com.management.library.domain.type.RentalStatus.PROCEEDING;
import static com.management.library.domain.type.RentalStatus.RETURNED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.domain.book.Book;
import com.management.library.domain.book.BookInfo;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.Authority;
import com.management.library.domain.type.BookStatus;
import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.exception.ErrorCode;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class BookRentalRepositoryTest {

  @Autowired
  private BookRentalRepository bookRentalRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private MemberRepository memberRepository;

  @DisplayName("회원 도서 회원 번호로 모든 대여 기록을 조회할 수 있다. 조회는 한 페이지에 5개까지 표시된다.")
  @Test
  public void findRentalPageByMemberCode() throws Exception {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("kim", "123457");
    Member member3 = createMember("kim", "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDate rentalDate1 = LocalDate.of(2023, 6, 21);
    LocalDate rentalDate2 = LocalDate.of(2023, 7, 1);
    LocalDate rentalDate3 = LocalDate.of(2023, 7, 21);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, AVAILABLE);
    Rental rental2 = createRental(book2, member1, RETURNED, rentalDate1, UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental4 = createRental(book4, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental5 = createRental(book5, member1, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental6 = createRental(book6, member1, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental7 = createRental(book1, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental8 = createRental(book2, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental9 = createRental(book3, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental10 = createRental(book4, member2, PROCEEDING, rentalDate3, AVAILABLE);

    bookRentalRepository.saveAll(List.of(
        rental1, rental2, rental3, rental4, rental5,
        rental6, rental7, rental8, rental9, rental10
    ));

    PageRequest pageRequest1 = PageRequest.of(0, 5);
    PageRequest pageRequest2 = PageRequest.of(1, 5);
    BookRentalSearchCond cond = new BookRentalSearchCond();

    // when
    Page<RentalServiceResponseDto> result1 = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123456", pageRequest1);
    List<RentalServiceResponseDto> content1 = result1.getContent();

    Page<RentalServiceResponseDto> result2 = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123456", pageRequest2);
    List<RentalServiceResponseDto> content2 = result2.getContent();

    // then
    assertThat(content1).hasSize(5)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", rentalDate1, rentalDate1.plusDays(14), AVAILABLE, RETURNED),
            tuple("jpa2", rentalDate1, rentalDate1.plusDays(14), UNAVAILABLE, RETURNED),
            tuple("spring", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, RETURNED),
            tuple("spring2", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, RETURNED),
            tuple("docker", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );

    assertThat(content2).hasSize(1)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .contains(
            tuple("docker2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );
  }

  @DisplayName("반납이 완료된 대여 기록을 조회한다.")
  @Test
  public void findRentalPageByMemberCodeWithReturned() throws Exception {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("kim", "123457");
    Member member3 = createMember("kim", "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDate rentalDate1 = LocalDate.of(2023, 6, 21);
    LocalDate rentalDate2 = LocalDate.of(2023, 7, 1);
    LocalDate rentalDate3 = LocalDate.of(2023, 7, 21);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, AVAILABLE);
    Rental rental2 = createRental(book2, member1, RETURNED, rentalDate1, UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental4 = createRental(book4, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental5 = createRental(book5, member1, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental6 = createRental(book6, member1, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental7 = createRental(book1, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental8 = createRental(book2, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental9 = createRental(book3, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental10 = createRental(book4, member2, PROCEEDING, rentalDate3, AVAILABLE);

    bookRentalRepository.saveAll(List.of(
        rental1, rental2, rental3, rental4, rental5,
        rental6, rental7, rental8, rental9, rental10
    ));

    PageRequest pageRequest = PageRequest.of(0, 5);

    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(RETURNED);

    // when
    Page<RentalServiceResponseDto> result = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123456", pageRequest);
    List<RentalServiceResponseDto> content = result.getContent();

    // then
    assertThat(content).hasSize(4)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", rentalDate1, rentalDate1.plusDays(14), AVAILABLE, RETURNED),
            tuple("jpa2", rentalDate1, rentalDate1.plusDays(14), UNAVAILABLE, RETURNED),
            tuple("spring", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, RETURNED),
            tuple("spring2", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, RETURNED)
        );
  }

  @DisplayName("대여 진행 중인 대여 기록을 조회한다.")
  @Test
  public void findRentalPageByMemberCodeWithProceeding() throws Exception {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("kim", "123457");
    Member member3 = createMember("kim", "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDate rentalDate1 = LocalDate.of(2023, 6, 21);
    LocalDate rentalDate2 = LocalDate.of(2023, 7, 1);
    LocalDate rentalDate3 = LocalDate.of(2023, 7, 21);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, AVAILABLE);
    Rental rental2 = createRental(book2, member1, RETURNED, rentalDate1, UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental4 = createRental(book4, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental5 = createRental(book5, member1, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental6 = createRental(book6, member1, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental7 = createRental(book1, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental8 = createRental(book2, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental9 = createRental(book3, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental10 = createRental(book4, member2, PROCEEDING, rentalDate3, AVAILABLE);

    bookRentalRepository.saveAll(List.of(
        rental1, rental2, rental3, rental4, rental5,
        rental6, rental7, rental8, rental9, rental10
    ));

    PageRequest pageRequest = PageRequest.of(0, 5);

    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(PROCEEDING);

    // when
    Page<RentalServiceResponseDto> result = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123457", pageRequest);
    List<RentalServiceResponseDto> content = result.getContent();

    // then
    assertThat(content).hasSize(4)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("jpa2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("spring", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("spring2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );
  }

  @DisplayName("연체된 대여 기록을 조회한다.")
  @Test
  public void findRentalPageByMemberCodeWithOverdue() throws Exception {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("kim", "123457");
    Member member3 = createMember("kim", "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDate rentalDate1 = LocalDate.of(2023, 6, 21);
    LocalDate rentalDate2 = LocalDate.of(2023, 7, 1);
    LocalDate rentalDate3 = LocalDate.of(2023, 7, 21);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, AVAILABLE);
    Rental rental2 = createRental(book2, member1, RETURNED, rentalDate1, UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental4 = createRental(book4, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental5 = createRental(book5, member1, OVERDUE, rentalDate3, AVAILABLE);
    Rental rental6 = createRental(book6, member1, OVERDUE, rentalDate3, AVAILABLE);
    Rental rental7 = createRental(book1, member2, OVERDUE, rentalDate3, AVAILABLE);
    Rental rental8 = createRental(book2, member2, OVERDUE, rentalDate3, AVAILABLE);
    Rental rental9 = createRental(book3, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental10 = createRental(book4, member2, PROCEEDING, rentalDate3, AVAILABLE);

    bookRentalRepository.saveAll(List.of(
        rental1, rental2, rental3, rental4, rental5,
        rental6, rental7, rental8, rental9, rental10
    ));

    PageRequest pageRequest = PageRequest.of(0, 5);

    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(OVERDUE);

    // when
    Page<RentalServiceResponseDto> result = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123456", pageRequest);
    List<RentalServiceResponseDto> content = result.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("docker", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, OVERDUE),
            tuple("docker2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, OVERDUE)
        );
  }

  @DisplayName("저장된 모든 도서 대여 기록을 조회할 수 있다. 대여 상태로 필터링할 수 있고 한 페이지에는 최대 5개의 기록이 들어있다.")
  @Test
  public void findAllWithPage() throws Exception {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("kim", "123457");
    Member member3 = createMember("kim", "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDate rentalDate1 = LocalDate.of(2023, 6, 21);
    LocalDate rentalDate2 = LocalDate.of(2023, 7, 1);
    LocalDate rentalDate3 = LocalDate.of(2023, 7, 21);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, AVAILABLE);
    Rental rental2 = createRental(book2, member1, RETURNED, rentalDate1, UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental4 = createRental(book4, member1, RETURNED, rentalDate2, AVAILABLE);
    Rental rental5 = createRental(book5, member1, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental6 = createRental(book6, member1, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental7 = createRental(book1, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental8 = createRental(book2, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental9 = createRental(book3, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental10 = createRental(book4, member2, PROCEEDING, rentalDate3, AVAILABLE);

    bookRentalRepository.saveAll(List.of(
        rental1, rental2, rental3, rental4, rental5,
        rental6, rental7, rental8, rental9, rental10
    ));

    PageRequest pageRequest1 = PageRequest.of(0, 5);
    PageRequest pageRequest2 = PageRequest.of(1, 5);

    BookRentalSearchCond cond = new BookRentalSearchCond();

    // when
    Page<RentalServiceResponseDto> rentalList1 = bookRentalRepository.findAllWithPage(cond,
        pageRequest1);
    List<RentalServiceResponseDto> content1 = rentalList1.getContent();

    Page<RentalServiceResponseDto> rentalList2 = bookRentalRepository.findAllWithPage(cond,
        pageRequest2);
    List<RentalServiceResponseDto> content2 = rentalList2.getContent();

    // then
    assertThat(content1).hasSize(5)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", rentalDate1, rentalDate1.plusDays(14), AVAILABLE, RETURNED),
            tuple("jpa2", rentalDate1, rentalDate1.plusDays(14), UNAVAILABLE, RETURNED),
            tuple("spring", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, RETURNED),
            tuple("spring2", rentalDate2, rentalDate2.plusDays(14), AVAILABLE, RETURNED),
            tuple("docker", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );

    assertThat(content2).hasSize(5)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("docker2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("jpa", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("jpa2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("spring", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple("spring2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );
  }

  @DisplayName("저장된 모든 도서 대여 기록을 대여 상태로 필터링할 수 있다 한 페이지에는 최대 5개의 기록이 들어있다.")
  @Test
  public void findAllWithPageCond() throws Exception {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("kim", "123457");
    Member member3 = createMember("kim", "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDate rentalDate1 = LocalDate.of(2023, 6, 21);
    LocalDate rentalDate2 = LocalDate.of(2023, 7, 1);
    LocalDate rentalDate3 = LocalDate.of(2023, 7, 21);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, UNAVAILABLE);
    Rental rental2 = createRental(book2, member1, RETURNED, rentalDate1, UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, RETURNED, rentalDate2, UNAVAILABLE);
    Rental rental4 = createRental(book4, member1, RETURNED, rentalDate2, UNAVAILABLE);
    Rental rental5 = createRental(book5, member1, PROCEEDING, rentalDate3, UNAVAILABLE);
    Rental rental6 = createRental(book6, member1, PROCEEDING, rentalDate3, UNAVAILABLE);
    Rental rental7 = createRental(book1, member2, PROCEEDING, rentalDate3, UNAVAILABLE);
    Rental rental8 = createRental(book2, member2, PROCEEDING, rentalDate3, UNAVAILABLE);
    Rental rental9 = createRental(book3, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental10 = createRental(book4, member2, PROCEEDING, rentalDate3, AVAILABLE);

    bookRentalRepository.saveAll(List.of(
        rental1, rental2, rental3, rental4, rental5,
        rental6, rental7, rental8, rental9, rental10
    ));

    PageRequest pageRequest1 = PageRequest.of(0, 5);
    PageRequest pageRequest2 = PageRequest.of(1, 5);

    BookRentalSearchCond cond = new BookRentalSearchCond();
    cond.setRentalStatus(PROCEEDING);

    // when
    Page<RentalServiceResponseDto> rentalList1 = bookRentalRepository.findAllWithPage(cond,
        pageRequest1);
    List<RentalServiceResponseDto> content1 = rentalList1.getContent();

    Page<RentalServiceResponseDto> rentalList2 = bookRentalRepository.findAllWithPage(cond,
        pageRequest2);
    List<RentalServiceResponseDto> content2 = rentalList2.getContent();

    // then
    assertThat(content1).hasSize(5)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("docker", rentalDate3, rentalDate3.plusDays(14), UNAVAILABLE, PROCEEDING),
            tuple("docker2", rentalDate3, rentalDate3.plusDays(14), UNAVAILABLE, PROCEEDING),
            tuple("jpa", rentalDate3, rentalDate3.plusDays(14), UNAVAILABLE, PROCEEDING),
            tuple("jpa2", rentalDate3, rentalDate3.plusDays(14), UNAVAILABLE, PROCEEDING),
            tuple("spring", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );

    assertThat(content2).hasSize(1)
        .extracting("bookName", "rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple("spring2", rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );
  }

  @DisplayName("회원번호와 대여 상태 및 도서 정보로 대여 정보를 조회할 수 있다.")
  @Test
  public void findByBookInfoAndStatus() throws Exception {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("kim", "123457");
    Member member3 = createMember("kim", "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDate rentalDate1 = LocalDate.of(2023, 6, 21);
    LocalDate rentalDate2 = LocalDate.of(2023, 7, 1);
    LocalDate rentalDate3 = LocalDate.of(2023, 7, 21);

    Rental rental1 = createRental(book1, member1, RETURNED, rentalDate1, UNAVAILABLE);
    Rental rental2 = createRental(book2, member1, RETURNED, rentalDate1, UNAVAILABLE);
    Rental rental3 = createRental(book3, member1, RETURNED, rentalDate2, UNAVAILABLE);
    Rental rental4 = createRental(book4, member1, RETURNED, rentalDate2, UNAVAILABLE);
    Rental rental5 = createRental(book5, member1, PROCEEDING, rentalDate3, UNAVAILABLE);
    Rental rental6 = createRental(book6, member1, PROCEEDING, rentalDate3, UNAVAILABLE);
    Rental rental7 = createRental(book1, member2, PROCEEDING, rentalDate3, UNAVAILABLE);
    Rental rental8 = createRental(book2, member2, PROCEEDING, rentalDate3, UNAVAILABLE);
    Rental rental9 = createRental(book3, member2, PROCEEDING, rentalDate3, AVAILABLE);
    Rental rental10 = createRental(book4, member2, PROCEEDING, rentalDate3, AVAILABLE);

    bookRentalRepository.saveAll(List.of(
        rental1, rental2, rental3, rental4, rental5,
        rental6, rental7, rental8, rental9, rental10
    ));

    // when
    Rental rental = bookRentalRepository.findByBookInfoAndStatus(member1.getMemberCode(), "docker",
            "kim", PROCEEDING)
        .orElseThrow(() -> new NoSuchElementExistsException(ErrorCode.RENTAL_NOT_EXISTS));

    // then
    assertThat(rental.getMember().getMemberCode()).isEqualTo("123456");
    assertThat(rental.getBook().getBookInfo())
        .extracting("title", "author")
        .contains(
            "docker", "kim"
        );
    assertThat(rental)
        .extracting("rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .contains(
            rentalDate3, rentalDate3.plusDays(14), UNAVAILABLE, PROCEEDING
        );

  }


  private static Rental createRental(Book book, Member member, RentalStatus rentalStatus,
      LocalDate rentalStartDate, ExtendStatus extendStatus) {
    return Rental.builder()
        .book(book)
        .member(member)
        .rentalStatus(rentalStatus)
        .rentalStartDate(rentalStartDate)
        .rentalEndDate(rentalStartDate.plusDays(14))
        .extendStatus(extendStatus)
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