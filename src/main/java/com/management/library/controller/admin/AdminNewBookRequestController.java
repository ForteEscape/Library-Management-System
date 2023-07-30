package com.management.library.controller.admin;

import com.management.library.controller.request.newbook.NewBookRequestControllerDto;
import com.management.library.controller.result.newbook.dto.NewBookResultControllerDto;
import com.management.library.dto.RequestSearchCond;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;
import com.management.library.service.result.newbook.NewBookResultService;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Request;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

  // 신간 요구사항 조회
  @GetMapping
  public Page<Response> getNewBookRequests(RequestSearchCond cond,
      Pageable pageable) {
    return newBookService.getAllNewBookRequest(cond, pageable);
  }

  // 신간 요구사항 세부 조회
  @GetMapping("/{requestId}")
  public NewBookRequestControllerDto.Response getNewBookRequestDetail(
      @PathVariable("requestId") Long requestId
  ) {
    NewBookRequestServiceDto.Response response = newBookService.getNewBookRequestDetail(requestId);

    return NewBookRequestControllerDto.Response.of(response);
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
