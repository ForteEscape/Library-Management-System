package com.management.library.repository.book;

import com.management.library.domain.book.Book;
import com.management.library.dto.BookSearchCond;
import com.management.library.service.book.response.BookServiceResponseDto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {

  Page<BookServiceResponseDto> bookSearch(BookSearchCond cond, Pageable pageable);

  Page<BookServiceResponseDto> findAllByBookTypeCode(int startCode, int endCode, Pageable pageable);

  Optional<Book> findByTitleAndAuthor(String title, String author);

}
