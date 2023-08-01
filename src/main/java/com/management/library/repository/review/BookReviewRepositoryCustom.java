package com.management.library.repository.review;

import com.management.library.domain.book.BookReview;
import com.management.library.service.review.dto.BookReviewOverviewDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookReviewRepositoryCustom {

  Page<BookReviewOverviewDto> findByMemberCode(String memberCode, Pageable pageable);

  Optional<BookReview> findReviewAndBookById(Long id);
}
