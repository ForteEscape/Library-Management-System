package com.management.library.controller.member;

import com.management.library.controller.member.dto.MemberUpdateControllerDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberUpdateServiceDto;
import com.management.library.service.query.MemberTotalInfoService;
import com.management.library.service.query.dto.MemberTotalInfoDto;
import com.management.library.service.query.dto.PasswordChangeDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
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
  @GetMapping
  public MemberTotalInfoDto getMemberData(Principal principal) {
    return memberTotalInfoService.getMemberTotalInfo(principal.getName());
  }

  // 회원 정보 변경
  @PutMapping
  public String updateMemberData(@RequestBody MemberUpdateControllerDto request,
      Principal principal) {

    return memberService.updateMemberData(MemberUpdateServiceDto.of(request), principal.getName());
  }

  // 회원 비밀번호 변경
  @PostMapping("/change-password")
  public String changeMemberPassword(@RequestBody PasswordChangeDto request, Principal principal) {
    return memberService.changePassword(principal.getName(), request);
  }
}
