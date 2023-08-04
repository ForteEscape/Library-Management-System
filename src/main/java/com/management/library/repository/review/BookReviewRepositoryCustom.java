package com.management.library.repository.review;

import com.management.library.domain.book.BookReview;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookReviewRepositoryCustom {

  Page<BookReviewOverviewDto> findByMemberCode(String memberCode, Pageable pageable);

  Optional<BookReview> findReviewAndBookById(Long id);

  Page<BookReviewOverviewDto> findReviewByBookTitle(Long bookId, Pageable pageable);

  Long countByReviewDate(LocalDate startDate, LocalDate endDate);
}
