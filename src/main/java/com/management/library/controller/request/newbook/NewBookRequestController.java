package com.management.library.controller.request.newbook;

import com.management.library.controller.request.newbook.dto.NewBookRequestControllerDto;
import com.management.library.dto.RequestSearchCond;
import com.management.library.service.query.NewBookTotalResponseService;
import com.management.library.service.query.dto.NewBookTotalResponseDto;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Request;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;
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
@RequestMapping("/new-book-requests")
public class NewBookRequestController {

  private final NewBookService newBookService;
  private final NewBookTotalResponseService newBookTotalResponseService;

  @GetMapping
  public Page<Response> getNewBookRequestList(RequestSearchCond cond, Pageable pageable) {
    return newBookService.getAllNewBookRequest(cond, pageable);
  }

  @GetMapping("/{requestId}")
  public NewBookTotalResponseDto getNewBookRequestDetail(
      @PathVariable("requestId") Long requestId) {
    return newBookTotalResponseService.getNewBookTotalResponse(requestId);
  }

  @PostMapping
  public NewBookRequestControllerDto.Response createNewBookRequest(
      @RequestBody NewBookRequestControllerDto.Request request,
      Principal principal
  ) {

    Response newBookRequest = newBookService.createNewBookRequest(Request.of(request),
        principal.getName());

    return NewBookRequestControllerDto.Response.of(newBookRequest);
  }
}