package com.management.library.domain;

import com.management.library.domain.type.BookStatus;
import com.management.library.dto.BookUpdateDto;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
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
  @GeneratedValue
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

  // 도서 상태 변경 메서드
  public void changeBookStatus(BookStatus bookStatus) {
    this.bookStatus = bookStatus;
  }

  // 도서 정보 변경 메서드
  public void changeBookData(BookUpdateDto.Request request) {
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
