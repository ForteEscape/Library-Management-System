package com.management.library.controller.admin;

import static com.management.library.controller.admin.dto.AdminControllerCreateDto.Request;
import static com.management.library.controller.admin.dto.AdminControllerCreateDto.Response;

import com.management.library.controller.admin.dto.AdminAllReplyDto;
import com.management.library.controller.admin.dto.AdminSignInDto;
import com.management.library.controller.request.management.dto.ManagementResultControllerDto;
import com.management.library.controller.request.newbook.dto.NewBookResultControllerDto;
import com.management.library.controller.dto.PageInfo;
import com.management.library.security.TokenProvider;
import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminServiceCreateDto;
import com.management.library.service.admin.dto.AdminSignInResultDto;
import com.management.library.service.result.management.ManagementResultService;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import com.management.library.service.result.newbook.NewBookResultService;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 기능 사용 컨트롤러
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {

  private final AdminService adminService;
  private final ManagementResultService managementResultService;
  private final NewBookResultService newBookResultService;
  private final TokenProvider tokenProvider;

  @PreAuthorize("hasRole('Authority.ROLE_ADMIN')")
  @PostMapping("/sign-up")
  public Response createAdmin(@RequestBody @Valid Request request) {
    AdminServiceCreateDto.Response admin = adminService.createAdmin(
        AdminServiceCreateDto.Request.of(request));

    return Response.of(admin);
  }

  @PostMapping("/sign-in")
  public ResponseEntity<?> signIn(@Valid @RequestBody AdminSignInDto signIn){
    AdminSignInResultDto authenticate = adminService.authenticate(signIn.getAdminEmail(),
        signIn.getPassword());

    String token = tokenProvider.generateToken(authenticate.getAdminEmail(),
        authenticate.getAuthority());

    return new ResponseEntity<>(token, HttpStatus.OK);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/my-management-results")
  public ResponseEntity<?> getManagementResult(
      Principal principal, Pageable pageable) {

    Page<ManagementResultCreateDto.Response> resultPage = managementResultService.getResultByAdminEmail(
        principal.getName(), pageable);
    List<ManagementResultCreateDto.Response> content = resultPage.getContent();

    List<ManagementResultControllerDto.Response> result = content.stream()
        .map(ManagementResultControllerDto.Response::of)
        .collect(Collectors.toList());

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    return new ResponseEntity<>(
        new AdminAllReplyDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/my-new-book-results")
  public ResponseEntity<?> getNewBookResult(Principal principal,
      Pageable pageable) {

    Page<NewBookResultCreateDto.Response> resultPage = newBookResultService.getResultByAdminEmail(
        principal.getName(), pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<NewBookResultCreateDto.Response> content = resultPage.getContent();

    List<NewBookResultControllerDto.Response> result = content.stream()
        .map(NewBookResultControllerDto.Response::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new AdminAllReplyDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

}
