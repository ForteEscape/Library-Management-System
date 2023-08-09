package com.management.library.controller.admin;

import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.RequestAllDto;
import com.management.library.controller.dto.RequestSearchCond;
import com.management.library.controller.request.management.dto.ManagementRequestOverviewDto;
import com.management.library.controller.request.management.dto.ManagementResultControllerDto;
import com.management.library.controller.request.management.dto.ManagementResultControllerDto.ManagementResultResponse;
import com.management.library.service.query.ManagementTotalResponseService;
import com.management.library.service.query.dto.ManagementTotalResponseDto;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;
import com.management.library.service.result.management.ManagementResultService;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@Api(tags = {"관리자 전용 운영 개선 요청 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/management-requests")
public class AdminManagementController {

  private final ManagementService managementService;
  private final ManagementResultService managementResultService;
  private final ManagementTotalResponseService managementTotalResponseService;

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  @ApiOperation(value = "운영 개선 요구사항 조회", notes = "등록된 운영 개선 요구사항들을 조회할 수 있다.")
  public ResponseEntity<?> getManagementRequest(RequestSearchCond cond,
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

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{requestId}")
  @ApiOperation(value = "운영 개선 요구사항 상세 조회", notes = "운영 개선 요구사항의 상세 정보를 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "requestId", value = "요청 id"),
  })
  public ManagementTotalResponseDto getManagementRequestDetail(
      @PathVariable("requestId") Long requestId) {

    return managementTotalResponseService.getManagementTotalData(requestId);
  }

  // 운영 개선 요구사항 답변 등록
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{requestId}/result")
  @ApiOperation(value = "운영 개선 요구사항 답변 등록", notes = "등록된 운영 개선 요구사항에 대해 답변을 등록할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "requestId", value = "요청 id"),
  })
  public ManagementResultResponse createManagementRequestResult(
      @PathVariable("requestId") Long requestId,
      @RequestBody @Valid ManagementResultControllerDto.ManagementResultRequest managementResultRequest,
      Principal principal
  ) {

    ManagementResultCreateDto.Response result = managementResultService
        .createResult(ManagementResultCreateDto.Request.of(managementResultRequest), requestId,
            principal.getName());

    return ManagementResultResponse.of(result);
  }
}
