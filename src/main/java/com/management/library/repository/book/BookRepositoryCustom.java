package com.management.library.repository.book;

import com.management.library.domain.book.Book;
import com.management.library.controller.book.dto.BookSearchCond;
import com.management.library.service.book.dto.BookServiceCreateDto;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {

  Page<BookServiceCreateDto.Response> bookSearch(BookSearchCond cond, Pageable pageable);

  Page<BookServiceCreateDto.Response> findAllByBookTypeCode(int startCode, int endCode, Pageable pageable);

  Optional<Book> findByTitleAndAuthor(String title, String author);

  Long countByBookUnavailableStatus();

}
