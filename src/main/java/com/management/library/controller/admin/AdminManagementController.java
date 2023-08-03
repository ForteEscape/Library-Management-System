package com.management.library.controller.admin;

import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.RequestAllDto;
import com.management.library.controller.dto.RequestSearchCond;
import com.management.library.controller.request.management.dto.ManagementRequestOverviewDto;
import com.management.library.controller.request.management.dto.ManagementResultControllerDto;
import com.management.library.service.query.ManagementTotalResponseService;
import com.management.library.service.query.dto.ManagementTotalResponseDto;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;
import com.management.library.service.result.management.ManagementResultService;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/management-requests")
public class AdminManagementController {

  private final ManagementService managementService;
  private final ManagementResultService managementResultService;
  private final ManagementTotalResponseService managementTotalResponseService;

  // 운영 개선 요구사항 조회
  @GetMapping
  public ResponseEntity getManagementRequest(RequestSearchCond cond,
      Pageable pageable) {

    Page<Response> resultPage = managementService.getAllManagementRequest(cond, pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<ManagementRequestOverviewDto> result = resultPage.getContent().stream()
        .map(ManagementRequestOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new RequestAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 운영 개선 요구사항 상세 조회
  @GetMapping("/{requestId}")
  public ManagementTotalResponseDto getManagementRequestDetail(
      @PathVariable("requestId") Long requestId) {

    return managementTotalResponseService.getManagementTotalData(requestId);
  }

  // 운영 개선 요구사항 답변 등록
  @PostMapping("/{requestId}/result")
  public ManagementResultControllerDto.Response createManagementRequestResult(
      @PathVariable("requestId") Long requestId,
      @RequestBody @Valid ManagementResultControllerDto.Request request,
      Principal principal
  ) {

    ManagementResultCreateDto.Response result = managementResultService
        .createResult(ManagementResultCreateDto.Request.of(request), requestId,
            principal.getName());

    return ManagementResultControllerDto.Response.of(result);
  }
}
