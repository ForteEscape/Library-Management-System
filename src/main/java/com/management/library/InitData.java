package com.management.library;

import com.management.library.controller.admin.dto.AdminControllerCreateDto.AdminCreateRequest;
import com.management.library.controller.book.dto.BookControllerCreateDto.BookCreateRequest;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.repository.book.BookRepository;
import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminServiceCreateDto;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
@Slf4j
public class InitData {

  private final AdminService adminService;
  private final AdministratorRepository administratorRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final BookRepository bookRepository;
  private final BookService bookService;

  private static final String MONTHLY_RENTED_COUNT = "monthly-rented-count";
  private static final String MONTHLY_REVIEW_COUNT = "monthly-review-count";
  private static final String MONTHLY_BOOK_UNAVAILABLE_COUNT = "monthly-book-unavailable-count";
  private static final String YEARLY_RENTED_COUNT = "yearly-rented-count";
  private static final String YEARLY_REVIEW_COUNT = "yearly-review-count";
  private static final String YEARLY_BOOK_UNAVAILABLE_COUNT = "yearly-book-unavailable-count";

  // 현재 구조상 한 명의 administrator 는 필수적으로 존재해야 한다.
  @PostConstruct
  public void initData() {
    initAdminData();
    initBookData();
    initRedisData();
  }

  private void initAdminData() {
    AdminCreateRequest adminCreateRequest = new AdminCreateRequest("admin1@test.com", "admin1",
        "1234");

    boolean present = administratorRepository.findByEmail("admin1@test.com").isPresent();

    if (!present) {
      adminService.createAdmin(AdminServiceCreateDto.Request.of(adminCreateRequest));
    }
  }

  private void initBookData() {

    if (bookRepository.countBy() != 0) {
      return;
    }

    for (int i = 1; i <= 100; i++) {
      BookCreateRequest data = getData("book" + i, "author" + i, "publisher" + i, 1920 + i,
          "location" + i, i);

      bookService.createNewBook(BookServiceCreateDto.Request.of(data));
      log.info("sample book data created = {} ", data.getTitle());
    }
  }

  private void initRedisData() {

    for (int i = 2019; i <= 2022; i++) {
      int rentedSum = 0;
      int reviewedSum = 0;
      int bookUnavailableSum = 0;

      for (int j = 1; j <= 12; j++) {
        String key = i + "-" + j;
        redisTemplate.opsForHash().putIfAbsent(MONTHLY_RENTED_COUNT, key, String.valueOf(j + 5));
        redisTemplate.opsForHash().putIfAbsent(MONTHLY_REVIEW_COUNT, key, String.valueOf(j + 8));
        redisTemplate.opsForHash()
            .putIfAbsent(MONTHLY_BOOK_UNAVAILABLE_COUNT, key, String.valueOf(j + 1));

        rentedSum += (j + 5);
        reviewedSum += (j + 8);
        bookUnavailableSum += (j + 1);
      }

      redisTemplate.opsForHash()
          .putIfAbsent(YEARLY_RENTED_COUNT, String.valueOf(i), String.valueOf(rentedSum));
      redisTemplate.opsForHash()
          .putIfAbsent(YEARLY_REVIEW_COUNT, String.valueOf(i), String.valueOf(reviewedSum));
      redisTemplate.opsForHash()
          .putIfAbsent(YEARLY_BOOK_UNAVAILABLE_COUNT, String.valueOf(i), String.valueOf(bookUnavailableSum));
    }
  }

  private BookCreateRequest getData(String title, String author, String publisher,
      int publishedYear,
      String location, int typeCode) {
    return BookCreateRequest.builder()
        .title(title)
        .author(author)
        .publisher(publisher)
        .publishedYear(publishedYear)
        .location(location)
        .typeCode(typeCode)
        .build();
  }
}
