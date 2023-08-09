package com.management.library.controller.admin;

import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.RequestAllDto;
import com.management.library.controller.dto.RequestSearchCond;
import com.management.library.controller.request.newbook.dto.NewBookRequestOverviewDto;
import com.management.library.controller.request.newbook.dto.NewBookResultControllerDto;
import com.management.library.controller.request.newbook.dto.NewBookResultControllerDto.NewBookResultResponse;
import com.management.library.service.query.NewBookTotalResponseService;
import com.management.library.service.query.dto.NewBookTotalResponseDto;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;
import com.management.library.service.result.newbook.NewBookResultService;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Request;
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

@Api(tags = {"관리자 전용 신간 요청 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/new-book-requests")
public class AdminNewBookRequestController {

  private final NewBookService newBookService;
  private final NewBookResultService newBookResultService;
  private final NewBookTotalResponseService newBookTotalResponseService;

  // 신간 요구사항 조회
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  @ApiOperation(value = "신간 반입 요청사항 조회", notes = "등록된 신간 반입 요청사항들을 조회할 수 있다.")
  public ResponseEntity<?> getNewBookRequests(RequestSearchCond cond,
      Pageable pageable) {
    Page<Response> resultPage = newBookService.getAllNewBookRequest(cond, pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageNumber(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<NewBookRequestOverviewDto> result = resultPage.getContent().stream()
        .map(NewBookRequestOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new RequestAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 신간 요구사항 세부 조회
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{requestId}")
  @ApiOperation(value = "신간 반입 요구사항 상세 조회", notes = "특정 신관 반입 요구사항을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "requestId", value = "요청 id"),
  })
  public NewBookTotalResponseDto getNewBookRequestDetail(
      @PathVariable("requestId") Long requestId) {

    return newBookTotalResponseService.getNewBookTotalResponse(requestId);
  }

  // 신간 요구사항에 대한 답변 작성
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{requestId}/result")
  @ApiOperation(value = "요구사항 답변 작성", notes = "등록된 요구사항에 대한 답변을 등록한다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "requestId", value = "요청 id"),
      @ApiImplicitParam(name = "name", value = "접속 관리자 정보")
  })
  public NewBookResultResponse createNewBookRequestResult(
      @PathVariable("requestId") Long requestId,
      @RequestBody @Valid NewBookResultControllerDto.NewBookResultRequest newBookResultRequest,
      Principal principal
  ) {

    NewBookResultCreateDto.Response result = newBookResultService.createResult(Request.of(
            newBookResultRequest),
        requestId, principal.getName());

    return NewBookResultResponse.of(result);
  }
}
