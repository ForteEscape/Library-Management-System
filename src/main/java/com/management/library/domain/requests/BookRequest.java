package com.management.library.domain.requests;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Table(name = "book_requests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("book_request")
@SuperBuilder
public class BookRequest extends Request {

  private String requestBookName;
  private String requestContent;
}
