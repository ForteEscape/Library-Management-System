package com.management.library.controller.request.management;

import static com.management.library.controller.request.management.dto.ManagementRequestControllerDto.Request;
import static com.management.library.controller.request.management.dto.ManagementRequestControllerDto.Response;

import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.RequestSearchCond;
import com.management.library.controller.dto.ReviewAllDto;
import com.management.library.controller.request.management.dto.ManagementRequestOverviewDto;
import com.management.library.service.query.ManagementTotalResponseService;
import com.management.library.service.query.dto.ManagementTotalResponseDto;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/management-requests")
public class ManagementRequestController {

  private final ManagementService managementService;
  private final ManagementTotalResponseService managementTotalResponseService;

  // 운영 개선 사항 조회
  @GetMapping
  public ResponseEntity<?> getManagementRequestList(RequestSearchCond cond, Pageable pageable) {
    Page<ManagementRequestServiceDto.Response> resultPage = managementService.getAllManagementRequest(
        cond, pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<ManagementRequestOverviewDto> result = resultPage.getContent().stream()
        .map(ManagementRequestOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new ReviewAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 운영 개선 사항 상세 조회
  @GetMapping("/{requestId}")
  public ManagementTotalResponseDto getManagementRequestDetail(
      @PathVariable("requestId") Long requestId) {
    return managementTotalResponseService.getManagementTotalData(requestId);
  }

  // 운영 개선 사항 생성
  @PreAuthorize("hasRole('MEMBER')")
  @PostMapping
  public Response createManagementRequest(@RequestBody @Valid Request request,
      Principal principal) {
    ManagementRequestServiceDto.Response response = managementService.createManagementRequest(
        ManagementRequestServiceDto.Request.of(request), principal.getName());

    return Response.of(response);
  }
}
