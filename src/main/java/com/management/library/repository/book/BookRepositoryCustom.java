package com.management.library.repository.book;

import com.management.library.domain.book.Book;
import com.management.library.dto.BookSearchCond;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepositoryCustom {

  Page<Book> bookSearch(BookSearchCond cond, Pageable pageable);

  Page<Book> findAllByBookTypeCode(int startCode, int endCode, Pageable pageable);

}
