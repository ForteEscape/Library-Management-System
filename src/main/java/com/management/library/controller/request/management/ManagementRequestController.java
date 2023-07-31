package com.management.library.controller.request.management;

import static com.management.library.controller.request.management.dto.ManagementRequestControllerDto.Request;
import static com.management.library.controller.request.management.dto.ManagementRequestControllerDto.Response;

import com.management.library.dto.RequestSearchCond;
import com.management.library.service.query.ManagementTotalResponseService;
import com.management.library.service.query.dto.ManagementTotalResponseDto;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
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
@RequestMapping("/management-requests")
public class ManagementRequestController {

  private final ManagementService managementService;
  private final ManagementTotalResponseService managementTotalResponseService;

  @GetMapping
  public Page<ManagementRequestServiceDto.Response> getManagementRequestList(RequestSearchCond cond,
      Pageable pageable) {
    return managementService.getAllManagementRequest(cond, pageable);
  }

  @GetMapping("/{requestId}")
  public ManagementTotalResponseDto getManagementRequestDetail(
      @PathVariable("requestId") Long requestId) {
    return managementTotalResponseService.getManagementTotalData(requestId);
  }

  @PostMapping
  public Response createManagementRequest(@RequestBody Request request, Principal principal) {
    ManagementRequestServiceDto.Response response = managementService.createManagementRequest(
        ManagementRequestServiceDto.Request.of(request), principal.getName());

    return Response.of(response);
  }
}
