package com.management.library.domain.book;

import static com.management.library.domain.type.BookStatus.*;

import com.management.library.domain.BaseEntity;
import com.management.library.domain.type.BookStatus;
import com.management.library.service.book.dto.BookServiceCreateDto;
import com.management.library.service.book.dto.BookServiceUpdateDto;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(indexes = @Index(name = "index__title__author__publisher", columnList = "title, author, publisher"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Book extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "book_id")
  private Long id;

  @Embedded
  private BookInfo bookInfo;

  private int typeCode;

  @Enumerated(EnumType.STRING)
  private BookStatus bookStatus;

  @Builder
  public Book(Long id, BookInfo bookInfo, int typeCode, BookStatus bookStatus) {
    this.id = id;
    this.bookInfo = bookInfo;
    this.typeCode = typeCode;
    this.bookStatus = bookStatus;
  }

  public static Book of(BookServiceCreateDto.Request request){
    return Book.builder()
        .bookInfo(
            BookInfo.of(
                request.getTitle(), request.getAuthor(), request.getPublisher(),
                request.getPublishedYear(), request.getLocation()
            )
        )
        .typeCode(request.getTypeCode())
        .bookStatus(AVAILABLE)
        .build();
  }

  // 도서 상태 변경 메서드
  public void changeBookStatus(BookStatus bookStatus) {
    this.bookStatus = bookStatus;
  }

  // 도서 정보 변경 메서드
  public void changeBookData(BookServiceUpdateDto.Request request) {
    this.typeCode = request.getTypeCode();
    this.bookInfo = BookInfo.builder()
        .title(request.getTitle())
        .author(request.getAuthor())
        .publisher(request.getPublisher())
        .location(request.getLocation())
        .publishedYear(request.getPublishedYear())
        .build();
  }
}
