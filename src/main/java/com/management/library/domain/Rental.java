package com.management.library.domain;

import com.management.library.domain.type.ExtendStatus;
import com.management.library.domain.type.RentalStatus;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Table(name = "rentals")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Rental extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "loan_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id")
  private Book book;

  private LocalDateTime rentalStartDate;
  private LocalDateTime rentalEndDate;

  @Enumerated(EnumType.STRING)
  private ExtendStatus extendStatus;

  @Enumerated(EnumType.STRING)
  private RentalStatus rentalStatus;

  @Builder
  public Rental(Long id, Member member, Book book, LocalDateTime rentalStartDate,
      LocalDateTime rentalEndDate, ExtendStatus extendStatus, RentalStatus rentalStatus) {
    this.id = id;
    this.member = member;
    this.book = book;
    this.rentalStartDate = rentalStartDate;
    this.rentalEndDate = rentalEndDate;
    this.extendStatus = extendStatus;
    this.rentalStatus = rentalStatus;
  }

  /**
   * 도서 대여 기한 연장
   * 해당 대여 데이터가 연장 가능한 데이터인지는 service 에서 검증하도록 한다.
   */
  public void extendRentalEndDate() {
    this.extendStatus = ExtendStatus.UNAVAILABLE;
    this.rentalEndDate = this.rentalEndDate.plusDays(7);
  }

  /**
   * 도서 대여 상태 변경
   * 반납 시 사용되는 로직으로 해당 대여가 정상 반납인지, 연체인지는 service 에서 검증하도록 한다.
   *
   * @param rentalStatus 바꿀 대여 상태 정보
   */
  public void changeRentalStatus(RentalStatus rentalStatus){
    this.rentalStatus = rentalStatus;
  }
}
