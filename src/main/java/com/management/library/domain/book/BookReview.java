package com.management.library.domain.book;

import com.management.library.domain.BaseEntity;
import com.management.library.domain.member.Member;
import com.management.library.service.review.dto.BookReviewServiceDto.Request;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "book_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BookReview extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "book_review_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")
  private Book book;

  @Column(nullable = false)
  private String reviewTitle;
  @Column(nullable = false)
  private String reviewContent;
  private int rate;

  @Builder
  private BookReview(Long id, Member member, Book book, String reviewTitle, String reviewContent,
      int rate) {
    this.id = id;
    this.member = member;
    this.book = book;
    this.reviewTitle = reviewTitle;
    this.reviewContent = reviewContent;
    this.rate = rate;
  }

  public static BookReview of(Request reviewRequest, Member member, Book book){
    return BookReview.builder()
        .member(member)
        .book(book)
        .reviewTitle(reviewRequest.getReviewTitle())
        .reviewContent(reviewRequest.getReviewContent())
        .rate(reviewRequest.getReviewRate())
        .build();
  }

  /**
   * 등록된 review 수정
   * 단 리뷰의 평점은 수정할 수 없도록 설정
   *
   * @param reviewTitle 수정할 리뷰 제목
   * @param content 수정할 리뷰 내용
   */
  public void changeReviewTitleAndContent(String reviewTitle, String content){
    this.reviewTitle = reviewTitle;
    this.reviewContent = content;
  }
}
