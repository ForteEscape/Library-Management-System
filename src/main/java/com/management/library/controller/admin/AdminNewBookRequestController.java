package com.management.library.controller.admin;

import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.RequestAllDto;
import com.management.library.controller.dto.RequestSearchCond;
import com.management.library.controller.request.newbook.dto.NewBookRequestControllerDto;
import com.management.library.controller.request.newbook.dto.NewBookResultControllerDto;
import com.management.library.service.query.NewBookTotalResponseService;
import com.management.library.service.query.dto.NewBookTotalResponseDto;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;
import com.management.library.service.result.newbook.NewBookResultService;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Request;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/new-book-requests")
public class AdminNewBookRequestController {

  private final NewBookService newBookService;
  private final NewBookResultService newBookResultService;
  private final NewBookTotalResponseService newBookTotalResponseService;

  // 신간 요구사항 조회
  @GetMapping
  public ResponseEntity getNewBookRequests(RequestSearchCond cond,
      Pageable pageable) {
    Page<Response> resultPage = newBookService.getAllNewBookRequest(cond, pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageNumber(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<NewBookRequestControllerDto.Response> result = resultPage.getContent().stream()
        .map(NewBookRequestControllerDto.Response::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new RequestAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 신간 요구사항 세부 조회
  @GetMapping("/{requestId}")
  public NewBookTotalResponseDto getNewBookRequestDetail(
      @PathVariable("requestId") Long requestId) {

    return newBookTotalResponseService.getNewBookTotalResponse(requestId);
  }

  // 신간 요구사항에 대한 답변 작성
  @PostMapping("/{requestId}/result")
  public NewBookResultControllerDto.Response createNewBookRequestResult(
      @PathVariable("requestId") Long requestId,
      @RequestBody NewBookResultControllerDto.Request request,
      Principal principal
  ) {

    NewBookResultCreateDto.Response result = newBookResultService.createResult(Request.of(request),
        requestId, principal.getName());

    return NewBookResultControllerDto.Response.of(result);
  }
}
