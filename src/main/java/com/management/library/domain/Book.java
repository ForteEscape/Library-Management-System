package com.management.library.domain;

import com.management.library.domain.type.BookStatus;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class Book extends BaseEntity{

  @Id
  @GeneratedValue
  @Column(name = "book_id")
  private Long id;

  @Embedded
  private BookInfo bookInfo;

  private int typeCode;

  @Enumerated(EnumType.STRING)
  private BookStatus bookStatus;
}
