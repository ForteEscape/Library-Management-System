package com.management.library.controller.dto;

import com.management.library.domain.type.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestSearchCond {

  private RequestStatus requestStatus;
}
