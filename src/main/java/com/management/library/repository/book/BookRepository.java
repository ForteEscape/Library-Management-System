package com.management.library.repository.book;

import com.management.library.domain.book.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long>, BookRepositoryCustom{

  int countBy();
}
