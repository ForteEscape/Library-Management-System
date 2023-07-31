package com.management.library.repository.review;

import com.management.library.domain.book.BookReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookReviewRepository extends JpaRepository<BookReview, Long>,
    BookReviewRepositoryCustom {

}
