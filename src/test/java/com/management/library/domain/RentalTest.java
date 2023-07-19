package com.management.library.domain;

import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.book.Book;
import com.management.library.domain.book.BookInfo;
import com.management.library.domain.member.Member;
import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.BookStatus;
import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.MemberRentalStatus;
import com.management.library.domain.type.RentalStatus;
import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Slf4j
class RentalTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("도서 대여 엔티티 테스트")
  void rentalEntityTest(){
    // given
    Member userA = Member.builder()
        .name("userA")
        .memberRentalStatus(MemberRentalStatus.RENTAL_AVAILABLE)
        .build();

    em.persist(userA);

    Book book = Book.builder()
        .bookInfo(new BookInfo("book", "author", "publisher", "location", 2002))
        .bookStatus(BookStatus.AVAILABLE)
        .typeCode(835)
        .build();

    em.persist(book);

    Rental rental = Rental.builder()
        .rentalStatus(RentalStatus.PROCEEDING)
        .member(userA)
        .book(book)
        .extendStatus(ExtendStatus.AVAILABLE)
        .rentalStartDate(LocalDateTime.now())
        .rentalEndDate(LocalDateTime.now().plusDays(14))
        .build();

    em.persist(rental);

    // when
    Rental result = em.find(Rental.class, rental.getId());

    // then
    assertThat(result).isEqualTo(rental);
  }

  @Test
  @DisplayName("도서 대여 기한 연장 테스트")
  void extendRentalEndDateTest(){
    // given
    Member userA = Member.builder()
        .name("userA")
        .memberRentalStatus(MemberRentalStatus.RENTAL_AVAILABLE)
        .build();

    em.persist(userA);

    Book book = Book.builder()
        .bookInfo(new BookInfo("book", "author", "publisher", "location", 2002))
        .bookStatus(BookStatus.AVAILABLE)
        .typeCode(835)
        .build();

    em.persist(book);

    LocalDateTime rentalDate = LocalDateTime.now().withNano(0);
    Rental rental = Rental.builder()
        .rentalStatus(RentalStatus.PROCEEDING)
        .member(userA)
        .book(book)
        .extendStatus(ExtendStatus.AVAILABLE)
        .rentalStartDate(rentalDate)
        .rentalEndDate(rentalDate.plusDays(14))
        .build();

    em.persist(rental);

    em.flush();
    em.clear();

    // when
    Rental rental1 = em.find(Rental.class, rental.getId());
    rental1.extendRentalEndDate();

    Rental result = em.find(Rental.class, rental.getId());

    // then
    assertThat(result.getRentalEndDate()).isEqualTo(rental.getRentalEndDate().plusDays(7));
  }

  @Test
  @DisplayName("도서 대여 상태 변경 테스트")
  void changeRentalStatusTest(){
    // given
    Member userA = Member.builder()
        .name("userA")
        .memberRentalStatus(MemberRentalStatus.RENTAL_AVAILABLE)
        .build();

    em.persist(userA);

    Book book = Book.builder()
        .bookInfo(new BookInfo("book", "author", "publisher", "location", 2002))
        .bookStatus(BookStatus.AVAILABLE)
        .typeCode(835)
        .build();

    em.persist(book);

    LocalDateTime rentalDate = LocalDateTime.now().withNano(0);
    Rental rental = Rental.builder()
        .rentalStatus(RentalStatus.PROCEEDING)
        .member(userA)
        .book(book)
        .extendStatus(ExtendStatus.AVAILABLE)
        .rentalStartDate(rentalDate)
        .rentalEndDate(rentalDate.plusDays(14))
        .build();

    em.persist(rental);

    // when
    Rental rental1 = em.find(Rental.class, rental.getId());
    rental1.changeRentalStatus(RentalStatus.RETURNED);


  }
}