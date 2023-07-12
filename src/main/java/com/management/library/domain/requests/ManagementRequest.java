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
@DiscriminatorValue("management_request")
@Table(name = "management_requests")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class ManagementRequest extends Request {

  private String title;
  private String content;
}
