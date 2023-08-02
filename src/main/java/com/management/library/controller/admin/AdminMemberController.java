package com.management.library.controller.admin;

import com.management.library.controller.admin.dto.MemberSearchCond;
import com.management.library.controller.member.dto.MemberControllerCreateDto;
import com.management.library.controller.member.dto.MemberReadControllerDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.member.dto.MemberReadServiceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/members")
public class AdminMemberController {

  private final MemberService memberService;

  // 회원 생성
  @PostMapping
  public MemberControllerCreateDto.Response createMember(
      @RequestBody MemberControllerCreateDto.Request request
  ) {
    MemberCreateServiceDto.Response response = memberService.createMember(
        MemberCreateServiceDto.Request.of(request));

    return MemberControllerCreateDto.Response.of(response);
  }

  // 회원 전체 조회
  @GetMapping
  public Page<MemberReadServiceDto> getMemberList(MemberSearchCond cond, Pageable pageable) {
    return memberService.getMemberDataList(cond, pageable);
  }

  // 회원 상세 정보 조회
  @GetMapping("/{memberId}")
  public MemberReadControllerDto getMemberDetail(@PathVariable("memberId") Long memberId) {
    return MemberReadControllerDto.of(memberService.getMemberData(memberId));
  }

  // 회원 삭제
  @DeleteMapping("/{memberId}")
  public String deleteMember(@PathVariable("memberId") Long memberId) {
    return memberService.deleteMemberData(memberId);
  }

  // 회원 패스워드 초기화
  @PostMapping("/{memberId}/init-password")
  public String memberPasswordInit(@PathVariable("memberId") Long memberId) {
    return memberService.initMemberPassword(memberId);
  }
}
