package com.management.library.controller.member;

import com.management.library.controller.member.dto.MemberControllerUpdateDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberServiceUpdateDto;
import com.management.library.service.query.MemberTotalInfoService;
import com.management.library.service.query.dto.MemberTotalInfoDto;
import com.management.library.service.query.dto.PasswordChangeDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-info")
public class MemberController {

  private final MemberService memberService;
  private final MemberTotalInfoService memberTotalInfoService;

  // 마이페이지
  @PreAuthorize("hasRole('MEMBER')")
  @GetMapping
  public MemberTotalInfoDto getMemberData(Principal principal) {
    return memberTotalInfoService.getMemberTotalInfo(principal.getName());
  }

  // 회원 정보 변경
  @PreAuthorize("hasRole('MEMBER')")
  @PutMapping
  public String updateMemberData(@RequestBody @Valid MemberControllerUpdateDto request,
      Principal principal) {

    return memberService.updateMemberData(MemberServiceUpdateDto.of(request), principal.getName());
  }

  // 회원 비밀번호 변경
  @PreAuthorize("hasRole('MEMBER')")
  @PostMapping("/change-password")
  public String changeMemberPassword(@RequestBody @Valid PasswordChangeDto request, Principal principal) {
    return memberService.changePassword(principal.getName(), request);
  }
}
