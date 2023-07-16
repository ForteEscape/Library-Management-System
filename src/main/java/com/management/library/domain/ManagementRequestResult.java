package com.management.library.domain;

import com.management.library.domain.type.RequestStatus;
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
public class ManagementRequestResult extends BaseEntity{

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "management_reqeust_result_id")
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "management_request_id")
  private ManagementRequest managementRequest;

  @ManyToOne
  @JoinColumn(name = "administrator_id")
  private Administrator administrator;

  private String resultPostTitle;
  private String resultPostContent;

  @Enumerated(EnumType.STRING)
  private RequestStatus result;

  @Builder
  public ManagementRequestResult(Long id, ManagementRequest managementRequest,
      Administrator administrator, String resultPostTitle, String resultPostContent,
      RequestStatus result) {
    this.id = id;
    this.managementRequest = managementRequest;
    this.administrator = administrator;
    this.resultPostTitle = resultPostTitle;
    this.resultPostContent = resultPostContent;
    this.result = result;
  }
}
