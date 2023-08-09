package com.management.library.controller.member;

import com.management.library.controller.member.dto.MemberControllerUpdateDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberServiceUpdateDto;
import com.management.library.service.query.MemberTotalInfoService;
import com.management.library.service.query.dto.MemberTotalInfoDto;
import com.management.library.service.query.dto.PasswordChangeDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"회원 전용 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-info")
public class MemberController {

  private final MemberService memberService;
  private final MemberTotalInfoService memberTotalInfoService;

  // 마이페이지
  @PreAuthorize("hasRole('MEMBER')")
  @GetMapping
  @ApiOperation(value = "회원 마이페이지 조회", notes = "회원의 정보 조회")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속한 회원 정보")
  })
  public MemberTotalInfoDto getMemberData(Principal principal) {
    return memberTotalInfoService.getMemberTotalInfo(principal.getName());
  }

  // 회원 정보 변경
  @PreAuthorize("hasRole('MEMBER')")
  @PutMapping
  @ApiOperation(value = "회원 정보 변경", notes = "회원의 이름 또는 주소 변경 기능")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속한 회원 정보")
  })
  public String updateMemberData(@RequestBody @Valid MemberControllerUpdateDto request,
      Principal principal) {

    return memberService.updateMemberData(MemberServiceUpdateDto.of(request), principal.getName());
  }

  // 회원 비밀번호 변경
  @PreAuthorize("hasRole('MEMBER')")
  @PostMapping("/change-password")
  @ApiOperation(value = "회원 패스워드 변경", notes = "회원의 패스워드 변경")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속한 회원 정보")
  })
  public String changeMemberPassword(@RequestBody @Valid PasswordChangeDto request,
      Principal principal) {
    return memberService.changePassword(principal.getName(), request);
  }
}
