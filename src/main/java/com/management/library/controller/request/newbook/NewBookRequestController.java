package com.management.library.controller.request.newbook;

import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.ReviewAllDto;
import com.management.library.controller.request.newbook.dto.NewBookRequestControllerDto;
import com.management.library.controller.dto.RequestSearchCond;
import com.management.library.controller.request.newbook.dto.NewBookRequestOverviewDto;
import com.management.library.service.query.NewBookTotalResponseService;
import com.management.library.service.query.dto.NewBookTotalResponseDto;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Request;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;
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

@Api(tags = {"신간 반입 요청 조회 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/new-book-requests")
public class NewBookRequestController {

  private final NewBookService newBookService;
  private final NewBookTotalResponseService newBookTotalResponseService;

  // 신간 요청 목록 조회
  @GetMapping
  @ApiOperation(value = "신간 요청 목록 조회", notes = "신간 요청 목록들을 조회할 수 있다.")
  public ResponseEntity<?> getNewBookRequestList(RequestSearchCond cond, Pageable pageable) {
    Page<Response> resultPage = newBookService.getAllNewBookRequest(cond, pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<NewBookRequestOverviewDto> result = resultPage.getContent().stream()
        .map(NewBookRequestOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new ReviewAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 신간 요청 상세 조회
  @GetMapping("/{requestId}")
  @ApiOperation(value = "신간 요청 상세 조회", notes = "신간 요청 세부사항을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "requestId", value = "요청 id")
  })
  public NewBookTotalResponseDto getNewBookRequestDetail(
      @PathVariable("requestId") Long requestId) {
    return newBookTotalResponseService.getNewBookTotalResponse(requestId);
  }

  // 신간 요청 생성
  @PreAuthorize("hasRole('MEMBER')")
  @PostMapping
  @ApiOperation(value = "신간 요청 생성", notes = "신간 요청을 생성할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속한 회원 정보")
  })
  public NewBookRequestControllerDto.Response createNewBookRequest(
      @RequestBody @Valid NewBookRequestControllerDto.Request request,
      Principal principal
  ) {

    Response newBookRequest = newBookService.createNewBookRequest(Request.of(request),
        principal.getName());

    return NewBookRequestControllerDto.Response.of(newBookRequest);
  }
}
