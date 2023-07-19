package com.management.library.domain.book;

import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class BookInfo {

  private String title;
  private String author;
  private String publisher;
  private String location;
  private int publishedYear;

  @Builder
  public BookInfo(String title, String author, String publisher, String location,
      int publishedYear) {
    this.title = title;
    this.author = author;
    this.publisher = publisher;
    this.location = location;
    this.publishedYear = publishedYear;
  }
}
