package com.management.library.controller.admin;

import com.management.library.controller.admin.dto.MemberSearchCond;
import com.management.library.controller.dto.MemberAllDto;
import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.member.dto.MemberControllerCreateDto;
import com.management.library.controller.member.dto.MemberControllerCreateDto.MemberCreateResponse;
import com.management.library.controller.member.dto.MemberOverviewDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberServiceCreateDto;
import com.management.library.service.member.dto.MemberServiceReadDto;
import com.management.library.service.query.MemberTotalInfoService;
import com.management.library.service.query.dto.MemberTotalInfoDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"관리자 전용 회원 관리 기능"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/members")
public class AdminMemberController {

  private final MemberService memberService;
  private final MemberTotalInfoService memberTotalInfoService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  @ApiOperation(value = "회원 생성 기능", notes = "회원 생성 기능")
  public MemberCreateResponse createMember(
      @RequestBody @Valid MemberControllerCreateDto.MemberCreateRequest memberCreateRequest
  ) {
    MemberServiceCreateDto.Response response = memberService.createMember(
        MemberServiceCreateDto.Request.of(memberCreateRequest));

    return MemberCreateResponse.of(response);
  }

  // 회원 전체 조회
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  @ApiOperation(value = "회원 전체 조회 기능", notes = "도서관에 가입된 회원 전체를 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "memberName", value = "회원 이름"),
      @ApiImplicitParam(name = "memberCode", value = "회원 코드")
  })
  public ResponseEntity<?> getMemberList(MemberSearchCond cond, Pageable pageable) {
    Page<MemberServiceReadDto> resultPage = memberService.getMemberDataList(cond, pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<MemberOverviewDto> result = resultPage.getContent().stream()
        .map(MemberOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new MemberAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 회원 상세 정보 조회
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{memberId}")
  @ApiOperation(value = "회원 상세 정보 조회", notes = "회원의 상세 정보를 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "memberId", value = "회원 id"),
  })
  public MemberTotalInfoDto getMemberDetail(@PathVariable("memberId") Long memberId) {
    String memberCode = memberService.getMemberData(memberId).getMemberCode();

    return memberTotalInfoService.getMemberTotalInfo(memberCode);
  }

  // 회원 삭제
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{memberId}")
  @ApiOperation(value = "회원 삭제", notes = "회원 정보를 삭제한다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "memberId", value = "회원 id"),
  })
  public String deleteMember(@PathVariable("memberId") Long memberId) {
    return memberService.deleteMemberData(memberId);
  }

  // 회원 패스워드 초기화
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{memberId}/init-password")
  @ApiOperation(value = "회원 패스워드 초기화", notes = "회원의 비밀번호를 초기화시킨다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "memberId", value = "회원 id"),
  })
  public String memberPasswordInit(@PathVariable("memberId") Long memberId) {
    return memberService.initMemberPassword(memberId);
  }
}
