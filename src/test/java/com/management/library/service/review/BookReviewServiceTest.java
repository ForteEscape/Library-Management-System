package com.management.library.service.review;

import static org.junit.jupiter.api.Assertions.*;

import com.management.library.AbstractContainerBaseTest;
import com.management.library.repository.book.BookRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.repository.review.BookReviewRepository;
import com.management.library.service.book.BookService;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceCreateDto.Response;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.RedisMemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.rental.RentalRedisService;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class BookReviewServiceTest extends AbstractContainerBaseTest {

  @Autowired
  private BookReviewService bookReviewService;
  @Autowired
  private MemberService memberService;
  @Autowired
  private BookService bookService;
  @Autowired
  private RentalService rentalService;
  @Autowired
  private RedisMemberService redisMemberService;
  @Autowired
  private RentalRedisService rentalRedisService;

  @Autowired
  private BookReviewRepository bookReviewRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private BookRepository bookRepository;
  @Autowired
  private BookRentalRepository bookRentalRepository;

  @Autowired
  private RedisTemplate<String, String> redisTemplate;

  private static final String RENTAL_REDIS_KEY = "rental-count";
  private static final String BOOK_RENTED_COUNT = "book-rented-count";

  @AfterEach
  void tearDown(){
    bookReviewRepository.deleteAllInBatch();
    bookRentalRepository.deleteAllInBatch();
    memberRepository.deleteAllInBatch();
    bookRepository.deleteAllInBatch();

    redisTemplate.delete("memberCode");
    redisTemplate.delete(RENTAL_REDIS_KEY);
    redisTemplate.delete(BOOK_RENTED_COUNT);
  }

  private RentalBookInfoDto createRentalData(Response createdBook) {
    return RentalBookInfoDto.builder()
        .bookTitle(createdBook.getTitle())
        .author(createdBook.getAuthor())
        .build();
  }

  private MemberCreateServiceDto.Request createMemberRequest(String name,
      String birthdayCode, String legion, String city, String street) {
    return MemberCreateServiceDto.Request.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }

  private BookServiceCreateDto.Request createBookRequest(String title, String author,
      String publisher, int publishedYear, String location, int typeCode) {
    return BookServiceCreateDto.Request.builder()
        .title(title)
        .author(author)
        .publisher(publisher)
        .publishedYear(publishedYear)
        .location(location)
        .typeCode(typeCode)
        .build();
  }
}