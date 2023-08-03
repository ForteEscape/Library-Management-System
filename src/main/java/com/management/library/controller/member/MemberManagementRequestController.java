package com.management.library.controller.member;

import com.management.library.controller.dto.MemberRequestAllDto;
import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.member.dto.MemberManagementRequestOverviewDto;
import com.management.library.service.query.ManagementTotalResponseService;
import com.management.library.service.query.dto.ManagementTotalResponseDto;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-info/management-requests")
public class MemberManagementRequestController {

  private final ManagementService managementService;
  private final ManagementTotalResponseService managementTotalResponseService;

  // 회원 운영 개선 요청 기록 조회
  @GetMapping
  public ResponseEntity getMemberManagementRequest(Principal principal, Pageable pageable) {
    Page<Response> resultPage = managementService.getMemberManagementRequest(
        principal.getName(), pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<MemberManagementRequestOverviewDto> result = resultPage.getContent().stream()
        .map(MemberManagementRequestOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new MemberRequestAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 운영 개선 요청 상세 조회(결과까지 같이 조회)
  @GetMapping("/{requestId}")
  public ManagementTotalResponseDto getMemberManagementRequestDetail(
      @PathVariable("requestId") Long requestId) {
    return managementTotalResponseService.getManagementTotalData(requestId);
  }
}
