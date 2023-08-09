package com.management.library.controller.admin;

import static com.management.library.controller.admin.dto.AdminControllerCreateDto.AdminCreateResponse;

import com.management.library.controller.admin.dto.AdminAllReplyDto;
import com.management.library.controller.admin.dto.AdminControllerCreateDto;
import com.management.library.controller.admin.dto.AdminSignInDto;
import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.request.management.dto.ManagementResultControllerDto.ManagementResultResponse;
import com.management.library.controller.request.newbook.dto.NewBookResultControllerDto.NewBookResultResponse;
import com.management.library.security.TokenProvider;
import com.management.library.service.admin.AdminService;
import com.management.library.service.admin.dto.AdminServiceCreateDto;
import com.management.library.service.admin.dto.AdminSignInResultDto;
import com.management.library.service.result.management.ManagementResultService;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import com.management.library.service.result.newbook.NewBookResultService;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 관리자 기능 사용 컨트롤러
 */

@Api(tags = {"관리자 생성 및 요청 결과 조회 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/admins")
public class AdminController {

  private final AdminService adminService;
  private final ManagementResultService managementResultService;
  private final NewBookResultService newBookResultService;
  private final TokenProvider tokenProvider;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/sign-up")
  @ApiOperation(value = "관리자 생성", notes = "관리자를 생성한다")
  public AdminCreateResponse createAdmin(@RequestBody @Valid AdminControllerCreateDto.AdminCreateRequest adminCreateRequest) {
    AdminServiceCreateDto.Response admin = adminService.createAdmin(
        AdminServiceCreateDto.Request.of(adminCreateRequest));

    return AdminCreateResponse.of(admin);
  }

  @PostMapping("/sign-in")
  @ApiOperation(value = "관리자 로그인", notes = "관리자의 이메일과 비밀번호로 로그인할 수 있다.")
  public ResponseEntity<?> signIn(@Valid @RequestBody AdminSignInDto signIn){
    AdminSignInResultDto authenticate = adminService.authenticate(signIn.getAdminEmail(),
        signIn.getPassword());

    String token = tokenProvider.generateToken(authenticate.getAdminEmail(),
        authenticate.getAuthority());

    return new ResponseEntity<>(token, HttpStatus.OK);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/my-management-results")
  @ApiOperation(value = "관리자의 답변 리스트 조회", notes = "관리자가 등록한 신간 요청 답변들을 조회합니다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속 관리자 정보"),
  })
  public ResponseEntity<?> getManagementResult(Principal principal, Pageable pageable) {

    Page<ManagementResultCreateDto.Response> resultPage = managementResultService.getResultByAdminEmail(
        principal.getName(), pageable);
    List<ManagementResultCreateDto.Response> content = resultPage.getContent();

    List<ManagementResultResponse> result = content.stream()
        .map(ManagementResultResponse::of)
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
  @ApiOperation(value = "관리자 신간 요청 답변 조회", notes = "관리자가 등록한 신간 요청 답변들을 조회합니다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속 관리자 정보"),
  })
  public ResponseEntity<?> getNewBookResult(Principal principal,
      Pageable pageable) {

    Page<NewBookResultCreateDto.Response> resultPage = newBookResultService.getResultByAdminEmail(
        principal.getName(), pageable);

    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<NewBookResultCreateDto.Response> content = resultPage.getContent();

    List<NewBookResultResponse> result = content.stream()
        .map(NewBookResultResponse::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new AdminAllReplyDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

}
