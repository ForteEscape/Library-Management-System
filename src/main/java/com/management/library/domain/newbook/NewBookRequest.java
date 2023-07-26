package com.management.library.domain.newbook;

import static com.management.library.domain.type.RequestStatus.*;

import com.management.library.domain.BaseEntity;
import com.management.library.domain.member.Member;
import com.management.library.domain.type.RequestStatus;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Request;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class NewBookRequest extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "new_book_request_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "member_id")
  private Member member;

  private String requestBookTitle;
  private String requestContent;

  @Enumerated(EnumType.STRING)
  private RequestStatus requestStatus;

  @Builder
  public NewBookRequest(Long id, Member member, String requestBookTitle, String requestContent,
      RequestStatus requestStatus) {
    this.id = id;
    this.member = member;
    this.requestBookTitle = requestBookTitle;
    this.requestContent = requestContent;
    this.requestStatus = requestStatus;
  }

  public static NewBookRequest of(Request request, Member member) {
    return NewBookRequest.builder()
        .requestBookTitle(request.getRequestBookTitle())
        .requestContent(request.getRequestContent())
        .member(member)
        .requestStatus(AWAIT)
        .build();
  }

  public void changeRequestStatus(RequestStatus requestStatus){
    this.requestStatus = requestStatus;
  }
}
