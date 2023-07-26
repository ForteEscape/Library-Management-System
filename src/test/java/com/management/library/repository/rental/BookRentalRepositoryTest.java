package com.management.library.repository.rental;

import static com.management.library.domain.type.ExtendStatus.*;
import static com.management.library.domain.type.MemberRentalStatus.*;
import static com.management.library.domain.type.RentalStatus.*;
import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.book.Book;
import com.management.library.domain.book.BookInfo;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.Authority;
import com.management.library.domain.type.BookStatus;
import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.MemberRentalStatus;
import com.management.library.domain.type.RentalStatus;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.rental.dto.RentalServiceReadDto;
import java.time.LocalDateTime;
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
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("kim", RENTAL_AVAILABLE, "123457");
    Member member3 = createMember("kim", RENTAL_AVAILABLE, "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 6, 21, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate3 = LocalDateTime.of(2023, 7, 21, 0, 0);

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
    Page<RentalServiceReadDto> result1 = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123456", pageRequest1);
    List<RentalServiceReadDto> content1 = result1.getContent();

    Page<RentalServiceReadDto> result2 = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123456", pageRequest2);
    List<RentalServiceReadDto> content2 = result2.getContent();

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
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("kim", RENTAL_AVAILABLE, "123457");
    Member member3 = createMember("kim", RENTAL_AVAILABLE, "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 6, 21, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate3 = LocalDateTime.of(2023, 7, 21, 0, 0);

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
    Page<RentalServiceReadDto> result = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123456", pageRequest);
    List<RentalServiceReadDto> content = result.getContent();

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
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("kim", RENTAL_AVAILABLE, "123457");
    Member member3 = createMember("kim", RENTAL_AVAILABLE, "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 6, 21, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate3 = LocalDateTime.of(2023, 7, 21, 0, 0);

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
    Page<RentalServiceReadDto> result = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123457", pageRequest);
    List<RentalServiceReadDto> content = result.getContent();

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
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("kim", RENTAL_AVAILABLE, "123457");
    Member member3 = createMember("kim", RENTAL_AVAILABLE, "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 6, 21, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate3 = LocalDateTime.of(2023, 7, 21, 0, 0);

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
    Page<RentalServiceReadDto> result = bookRentalRepository.findRentalPageByMemberCode(
        cond, "123456", pageRequest);
    List<RentalServiceReadDto> content = result.getContent();

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
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("kim", RENTAL_AVAILABLE, "123457");
    Member member3 = createMember("kim", RENTAL_AVAILABLE, "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 6, 21, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate3 = LocalDateTime.of(2023, 7, 21, 0, 0);

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
    Page<Rental> rentalList1 = bookRentalRepository.findAllWithPage(cond, pageRequest1);
    List<Rental> content1 = rentalList1.getContent();

    Page<Rental> rentalList2 = bookRentalRepository.findAllWithPage(cond, pageRequest2);
    List<Rental> content2 = rentalList2.getContent();

    // then
    assertThat(content1).hasSize(5)
        .extracting("rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple(rentalDate1, rentalDate1.plusDays(14), AVAILABLE, RETURNED),
            tuple(rentalDate1, rentalDate1.plusDays(14), UNAVAILABLE, RETURNED),
            tuple(rentalDate2, rentalDate2.plusDays(14), AVAILABLE, RETURNED),
            tuple(rentalDate2, rentalDate2.plusDays(14), AVAILABLE, RETURNED),
            tuple(rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );

    assertThat(content1)
        .extracting(Rental::getBook)
        .extracting(Book::getBookInfo)
        .extracting(BookInfo::getTitle, BookInfo::getAuthor, BookInfo::getPublisher)
        .containsExactlyInAnyOrder(
            tuple("jpa", "kim", "publisher"),
            tuple("jpa2", "kim", "publisher"),
            tuple("spring", "kim", "publisher2"),
            tuple("spring2", "kim", "publisher2"),
            tuple("docker", "kim", "publisher3")
        );

    assertThat(content2).hasSize(5)
        .extracting("rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple(rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple(rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple(rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple(rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING),
            tuple(rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );

    assertThat(content2)
        .extracting(Rental::getBook)
        .extracting(Book::getBookInfo)
        .extracting(BookInfo::getTitle, BookInfo::getAuthor, BookInfo::getPublisher)
        .containsExactlyInAnyOrder(
            tuple("docker2", "kim", "publisher3"),
            tuple("jpa", "kim", "publisher"),
            tuple("jpa2", "kim", "publisher"),
            tuple("spring", "kim", "publisher2"),
            tuple("spring2", "kim", "publisher2")
        );
  }

  @DisplayName("저장된 모든 도서 대여 기록을 대여 상태로 필터링할 수 있다 한 페이지에는 최대 5개의 기록이 들어있다.")
  @Test
  public void findAllWithPageCond() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("kim", RENTAL_AVAILABLE, "123457");
    Member member3 = createMember("kim", RENTAL_AVAILABLE, "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    Book book1 = createBook("jpa", "kim", "publisher", "location1", 2017, 130);
    Book book2 = createBook("jpa2", "kim", "publisher", "location1", 2020, 130);
    Book book3 = createBook("spring", "kim", "publisher2", "location2", 2017, 135);
    Book book4 = createBook("spring2", "kim", "publisher2", "location2", 2020, 135);
    Book book5 = createBook("docker", "kim", "publisher3", "location3", 2017, 140);
    Book book6 = createBook("docker2", "kim", "publisher3", "location3", 2020, 140);

    bookRepository.saveAll(List.of(book1, book2, book3, book4, book5, book6));

    LocalDateTime rentalDate1 = LocalDateTime.of(2023, 6, 21, 0, 0);
    LocalDateTime rentalDate2 = LocalDateTime.of(2023, 7, 1, 0, 0);
    LocalDateTime rentalDate3 = LocalDateTime.of(2023, 7, 21, 0, 0);

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
    Page<Rental> rentalList1 = bookRentalRepository.findAllWithPage(cond, pageRequest1);
    List<Rental> content1 = rentalList1.getContent();

    Page<Rental> rentalList2 = bookRentalRepository.findAllWithPage(cond, pageRequest2);
    List<Rental> content2 = rentalList2.getContent();

    // then
    assertThat(content1).hasSize(5)
        .extracting("rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple(rentalDate3, rentalDate3.plusDays(14), UNAVAILABLE, PROCEEDING),
            tuple(rentalDate3, rentalDate3.plusDays(14), UNAVAILABLE, PROCEEDING),
            tuple(rentalDate3, rentalDate3.plusDays(14), UNAVAILABLE, PROCEEDING),
            tuple(rentalDate3, rentalDate3.plusDays(14), UNAVAILABLE, PROCEEDING),
            tuple(rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );

    assertThat(content1)
        .extracting(Rental::getBook)
        .extracting(Book::getBookInfo)
        .extracting(BookInfo::getTitle, BookInfo::getAuthor, BookInfo::getPublisher)
        .containsExactlyInAnyOrder(
            tuple("docker", "kim", "publisher3"),
            tuple("docker2", "kim", "publisher3"),
            tuple("jpa", "kim", "publisher"),
            tuple("jpa2", "kim", "publisher"),
            tuple("spring", "kim", "publisher2")
        );

    assertThat(content2).hasSize(1)
        .extracting("rentalStartDate", "rentalEndDate", "extendStatus", "rentalStatus")
        .containsExactlyInAnyOrder(
            tuple(rentalDate3, rentalDate3.plusDays(14), AVAILABLE, PROCEEDING)
        );

    assertThat(content2)
        .extracting(Rental::getBook)
        .extracting(Book::getBookInfo)
        .extracting(BookInfo::getTitle, BookInfo::getAuthor, BookInfo::getPublisher)
        .containsExactlyInAnyOrder(
            tuple("spring2", "kim", "publisher2")
        );
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

  private Book createBook(String title, String author, String publisher, String location,
      int publishedYear, int typeCode) {
    BookInfo bookInfo = createBookInfo(title, author, publisher, location, publishedYear);

    return Book.builder()
        .bookInfo(bookInfo)
        .bookStatus(BookStatus.AVAILABLE)
        .typeCode(typeCode)
        .build();
  }

  private static Member createMember(String name, MemberRentalStatus memberRentalStatus,
      String memberCode) {
    Address address = Address.builder()
        .legion("경상남도")
        .city("김해시")
        .street("삼계로")
        .build();

    return Member.builder()
        .name(name)
        .birthdayCode("980101")
        .memberRentalStatus(memberRentalStatus)
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