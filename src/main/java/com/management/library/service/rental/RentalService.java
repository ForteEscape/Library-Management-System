package com.management.library.service.rental;

import static com.management.library.domain.type.BookStatus.RENTAL;
import static com.management.library.domain.type.ExtendStatus.UNAVAILABLE;
import static com.management.library.domain.type.RentalStatus.OVERDUE;
import static com.management.library.domain.type.RentalStatus.RETURNED;
import static com.management.library.exception.ErrorCode.BOOK_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.MEMBER_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.MEMBER_STATUS_NOT_AVAILABLE;
import static com.management.library.exception.ErrorCode.OVERDUE_RENTAL_EXISTS;
import static com.management.library.exception.ErrorCode.RENTAL_ALREADY_EXTEND;
import static com.management.library.exception.ErrorCode.RENTAL_IS_OVERDUE;
import static com.management.library.exception.ErrorCode.RENTAL_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.UNABLE_TO_BOOK_RENTAL;
import static java.time.LocalDate.now;

import com.management.library.domain.book.Book;
import com.management.library.domain.member.Member;
import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.BookStatus;
import com.management.library.domain.type.RentalStatus;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.exception.RentalException;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import com.management.library.service.rental.dto.RentalDurationExtendDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.management.library.service.rental.dto.ReturnBookResponseDto;
import java.time.LocalDate;
import java.time.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RentalService {

  private final MemberRepository memberRepository;
  private final BookRepository bookRepository;
  private final BookRentalRepository rentalRepository;
  private final RentalRedisService rentalRedisService;

  /**
   * 도서 대여 기능 도서 대여 조건 1. 회원이 현재 도서 대여 가능한 상태인지(연체된 경우 도서 대여 불가) 2. 회원이 현재 대여 중인 도서가 2권 미만인지 동시성 문제의
   * 경우 현재 각 도서는 하나씩만 존재하고, 대여의 경우 현실 세계에서 이루어지는데 현실 세계에서 하나의 도서를 가지고 동시에 대여를 수행하는 경우는 존재하지 않으므로
   * 생각하지 않아도 될 것으로 생각됨
   * <p>
   * 연체되었으나 반납되지 않은 대여가 존재하는 경우에도 대여가 불가능함
   */
  @Transactional
  public RentalServiceResponseDto createBookRental(String memberCode, RentalBookInfoDto bookInfo,
      LocalDate rentalDate) {
    Member member = memberRepository.findByMemberCode(memberCode)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    Book book = bookRepository.findByTitleAndAuthor(bookInfo.getBookTitle(), bookInfo.getAuthor())
        .orElseThrow(() -> new NoSuchElementExistsException(BOOK_NOT_EXISTS));

    checkMemberRentalAvailable(memberCode);

    if (rentalRepository.existsByRentalStatus(OVERDUE)) {
      throw new RentalException(OVERDUE_RENTAL_EXISTS);
    }

    // 2권을 초과해서 도서를 대여할 경우 예외 발생
    rentalRedisService.checkMemberRentalBookCount(memberCode);
    book.changeBookStatus(RENTAL);

    Rental rental = rentalRepository.save(Rental.of(member, book, rentalDate));

    return RentalServiceResponseDto.of(rental);
  }

  private void checkMemberRentalAvailable(String memberCode) {
    if (rentalRedisService.checkMemberRentalPenalty(memberCode)) {
      throw new RentalException(UNABLE_TO_BOOK_RENTAL);
    }
  }

  /**
   * 도서 대여 기간 연장 기능 연장시킬 대여가 이미 연장이 이루어진 경우 연장할 수 없다. 반납 기한을 초과한 경우 연장을 할 수 없다. 회원이 현재 대출 불가능한 상태인
   * 경우 역시 연장할 수 없다. 이미 연체된 대여가 존재할 경우에도 연장할 수 없다.
   */
  @Transactional
  public RentalDurationExtendDto extendRentalDuration(String memberCode, Long rentalId) {
    Rental rental = rentalRepository.findById(rentalId)
        .orElseThrow(() -> new NoSuchElementExistsException(RENTAL_NOT_EXISTS));

    // 회원이 현재 대출 불가능한 상태인 경우 연장할 수 없다.
    if (rentalRedisService.checkMemberRentalPenalty(memberCode)) {
      throw new RentalException(MEMBER_STATUS_NOT_AVAILABLE);
    }

    // 연체 상태일 시 연장할 수 없다.
    if (rental.getRentalStatus() == OVERDUE) {
      throw new RentalException(RENTAL_IS_OVERDUE);
    }

    // 이미 연체된 대여가 존재할 시 연장할 수 없다.
    if (rentalRepository.existsByRentalStatus(OVERDUE)) {
      throw new RentalException(OVERDUE_RENTAL_EXISTS);
    }

    // 대여의 연장 가능 상태가 불가능일 경우 연장할 수 없다.
    if (rental.getExtendStatus() == UNAVAILABLE) {
      throw new RentalException(RENTAL_ALREADY_EXTEND);
    }

    rental.extendRentalEndDate();

    return RentalDurationExtendDto.of(rental.getRentalStartDate(), rental.getRentalEndDate());
  }

  /**
   * 도서 반납 기능 반납 기간을 초과한 경우 초과한 날 만큼 대여를 수행할 수 없다.
   */
  @Transactional
  public ReturnBookResponseDto returnBook(String memberCode, String bookTitle, String author) {
    Book book = bookRepository.findByTitleAndAuthor(bookTitle, author)
        .orElseThrow(() -> new NoSuchElementExistsException(BOOK_NOT_EXISTS));

    book.changeBookStatus(BookStatus.AVAILABLE);

    Rental rental = rentalRepository.findByBookInfoAndStatus(memberCode, bookTitle, author)
        .orElseThrow(() -> new NoSuchElementExistsException(RENTAL_NOT_EXISTS));

    RentalStatus result = RETURNED;
    String penaltyData = "NOT-OVERDUE";

    if (rental.getRentalStatus() == OVERDUE) {
      result = OVERDUE;
      Period overduePeriod = Period.between(rental.getRentalEndDate(), now());
      LocalDate penaltyEndDate = LocalDate.now().plusDays(overduePeriod.getDays());

      penaltyEndDate = rentalRedisService.addMemberOverdueData(memberCode, overduePeriod.getDays(),
          penaltyEndDate);

      penaltyData = penaltyEndDate.toString();
    }

    rentalRedisService.addMemberRentalBookCount(memberCode);
    rental.changeRentalStatus(RETURNED);

    return ReturnBookResponseDto.of(rental, result, penaltyData);
  }

  /**
   * 도서 대여 목록 조회 -> 관리자 전용
   */
  public Page<RentalServiceResponseDto> getRentalData(BookRentalSearchCond cond,
      Pageable pageable) {
    return rentalRepository.findAllWithPage(cond, pageable);
  }


}
