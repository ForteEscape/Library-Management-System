package com.management.library.service.review;

import static com.management.library.exception.ErrorCode.MEMBER_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.RENTAL_NOT_RETURNED;
import static com.management.library.exception.ErrorCode.RETURNED_RENTAL_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.REVIEW_ALREADY_EXISTS;
import static com.management.library.exception.ErrorCode.REVIEW_NOT_EXISTS;

import com.management.library.domain.book.BookReview;
import com.management.library.domain.member.Member;
import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.RentalStatus;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.InvalidAccessException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.repository.review.BookReviewRepository;
import com.management.library.service.review.dto.BookReviewDetailDto;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import com.management.library.service.review.dto.BookReviewServiceDto.Request;
import com.management.library.service.review.dto.BookReviewServiceDto.Response;
import com.management.library.service.review.dto.BookReviewUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookReviewService {

  private final BookReviewRepository bookReviewRepository;
  private final BookRentalRepository bookRentalRepository;
  private final MemberRepository memberRepository;
  private final BookReviewRedisService bookReviewRedisService;

  // 도서의 이름을 가지고 리뷰를 수행할 수 있도록 해야한다.
  // 도서의 이름을 가지면서 반환된 상태인 rental이 존재하는지 확인 -> 없다면 예외
  // 이미 리뷰를 적었는지 확인 -> 안했다면 등록
  // 평점 평균에 반영
  /**
   * 리뷰 생성을 위해서는 도서를 대여하고 반납해야 할 수 있으며 이미 리뷰한 도서에는 다시 리뷰할 수 없음.
   *
   * @param bookTitle     대여한 도서 이름
   * @param reviewRequest 리뷰 생성 요청 dto
   * @param memberCode    리뷰를 수행하는 회원 번호
   * @return 리뷰 dto
   */
  @Transactional
  public Response createReview(String bookTitle, Request reviewRequest, String memberCode) {
    Member member = memberRepository.findByMemberCode(memberCode)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    Rental rental = bookRentalRepository.findByMemberCodeAndBookTitle(memberCode, bookTitle)
        .orElseThrow(() -> new NoSuchElementExistsException(RETURNED_RENTAL_NOT_EXISTS));

    // 해당 도서가 반납되었는지 확인
    if (isRentalNotReturned(rental.getRentalStatus())) {
      throw new InvalidAccessException(RENTAL_NOT_RETURNED);
    }

    // 이미 리뷰했는지 redis cache 를 통해 확인
    if (isAlreadyReviewed(member, rental)) {
      throw new DuplicateException(REVIEW_ALREADY_EXISTS);
    }

    // 리뷰하지 않은 도서라면 리뷰했다고 등록
    bookReviewRedisService.addReviewCache(member.getMemberCode(),
        rental.getBook().getBookInfo().getTitle());

    // todo: 등록한 평점을 도서 평점 평균에 반영해야 한다.
    bookReviewRedisService.addReviewRate(bookTitle, reviewRequest.getReviewRate());

    BookReview review = BookReview.of(reviewRequest, member, rental.getBook());
    BookReview savedReview = bookReviewRepository.save(review);

    return Response.of(savedReview);
  }

  private boolean isRentalNotReturned(RentalStatus rentalStatus) {
    return rentalStatus != RentalStatus.RETURNED;
  }

  private boolean isAlreadyReviewed(Member member, Rental rental) {
    return bookReviewRedisService.getReviewCache(member.getMemberCode(),
        rental.getBook().getBookInfo().getTitle());
  }

  /**
   * 이미 등록한 리뷰 수정
   *
   * @param updateReviewRequest 수정할 내용 dto
   * @param reviewId            수정할 리뷰 id
   * @return 수정된 리뷰 dto
   */
  @Transactional
  public BookReviewUpdateDto.Response updateReview(BookReviewUpdateDto.Request updateReviewRequest,
      Long reviewId) {
    BookReview bookReview = bookReviewRepository.findById(reviewId)
        .orElseThrow(() -> new NoSuchElementExistsException(REVIEW_NOT_EXISTS));

    bookReview.changeReviewTitleAndContent(updateReviewRequest);

    return BookReviewUpdateDto.Response.of(bookReview);
  }

  /**
   * 특정 회원이 등록한 리뷰들 가져오기
   *
   * @param memberCode 특정 회원 코드
   * @param pageable   페이징 설정
   * @return 특정 회원이 등록한 리뷰 페이지 데이터
   */
  public Page<BookReviewOverviewDto> getMemberReviewDataList(String memberCode, Pageable pageable) {
    return bookReviewRepository.findByMemberCode(memberCode, pageable);
  }

  /**
   * 리뷰 단건 조회
   *
   * @param reviewId 리뷰 id
   * @return 상세 리뷰 정보
   */
  public BookReviewDetailDto getReviewData(Long reviewId) {
    BookReview bookReview = bookReviewRepository.findReviewAndBookById(reviewId)
        .orElseThrow(() -> new NoSuchElementExistsException(REVIEW_NOT_EXISTS));

    return BookReviewDetailDto.of(bookReview);
  }

  // 특정 도서의 리뷰 내역 조회
  public Page<BookReviewOverviewDto> getBookReviewList(Long bookId, Pageable pageable){
    return bookReviewRepository.findReviewByBookTitle(bookId, pageable);
  }
}
