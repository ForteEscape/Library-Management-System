package com.management.library.controller.request.management;

import static com.management.library.controller.request.management.dto.ManagementRequestControllerDto.ManagementCreateResponse;

import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.RequestSearchCond;
import com.management.library.controller.dto.ReviewAllDto;
import com.management.library.controller.request.management.dto.ManagementRequestControllerDto;
import com.management.library.controller.request.management.dto.ManagementRequestOverviewDto;
import com.management.library.service.query.ManagementTotalResponseService;
import com.management.library.service.query.dto.ManagementTotalResponseDto;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
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

@Api(tags = {"운영 개선 요청 조회 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/management-requests")
public class ManagementRequestController {

  private final ManagementService managementService;
  private final ManagementTotalResponseService managementTotalResponseService;

  // 운영 개선 사항 조회
  @GetMapping
  @ApiOperation(value = "운영 개선 사항 조회", notes = "운영 개선 사항들을 조회할 수 있다.")
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
  @ApiOperation(value = "운영 개선 사항 상세 조회", notes = "운영 개선 사항들을 상세하게 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "requestId", value = "요청 id")
  })
  public ManagementTotalResponseDto getManagementRequestDetail(
      @PathVariable("requestId") Long requestId) {
    return managementTotalResponseService.getManagementTotalData(requestId);
  }

  // 운영 개선 사항 생성
  @PreAuthorize("hasRole('MEMBER')")
  @PostMapping
  @ApiOperation(value = "운영 개선 사항 생성", notes = "운영 개선 요청 사항을 생성할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속한 회원 정보")
  })
  public ManagementCreateResponse createManagementRequest(@RequestBody @Valid ManagementRequestControllerDto.ManagementCreateRequest managementCreateRequest,
      Principal principal) {
    ManagementRequestServiceDto.Response response = managementService.createManagementRequest(
        ManagementRequestServiceDto.Request.of(managementCreateRequest), principal.getName());

    return ManagementCreateResponse.of(response);
  }
}
