package com.management.library.domain.newbook;

import com.management.library.domain.BaseEntity;
import com.management.library.domain.admin.Administrator;
import com.management.library.domain.type.RequestStatus;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Request;
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
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class NewBookRequestResult extends BaseEntity {

  @Id
  @GeneratedValue
  @Column(name = "new_book_request_result_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "new_book_request_id")
  private NewBookRequest newBookRequest;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "administrator_id")
  private Administrator administrator;

  private String resultPostTitle;
  private String resultPostContent;

  @Enumerated(EnumType.STRING)
  private RequestStatus result;

  @Builder
  public NewBookRequestResult(Long id, NewBookRequest newBookRequest, Administrator administrator,
      String resultPostTitle, String resultPostContent, RequestStatus result) {
    this.id = id;
    this.newBookRequest = newBookRequest;
    this.administrator = administrator;
    this.resultPostTitle = resultPostTitle;
    this.resultPostContent = resultPostContent;
    this.result = result;
  }

  public static NewBookRequestResult of(Request request, NewBookRequest newBookRequest,
      Administrator administrator) {
    return NewBookRequestResult.builder()
        .newBookRequest(newBookRequest)
        .administrator(administrator)
        .resultPostTitle(request.getResultTitle())
        .resultPostContent(request.getResultContent())
        .result(request.getResultStatus())
        .build();
  }
}
