package com.management.library.controller.admin;

import com.management.library.controller.admin.dto.MemberSearchCond;
import com.management.library.controller.dto.MemberAllDto;
import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.member.dto.MemberControllerCreateDto;
import com.management.library.controller.member.dto.MemberOverviewDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberServiceCreateDto;
import com.management.library.service.member.dto.MemberServiceReadDto;
import com.management.library.service.query.MemberTotalInfoService;
import com.management.library.service.query.dto.MemberTotalInfoDto;
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

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/members")
public class AdminMemberController {

  private final MemberService memberService;
  private final MemberTotalInfoService memberTotalInfoService;

  // 회원 생성
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public MemberControllerCreateDto.Response createMember(
      @RequestBody @Valid MemberControllerCreateDto.Request request
  ) {
    MemberServiceCreateDto.Response response = memberService.createMember(
        MemberServiceCreateDto.Request.of(request));

    return MemberControllerCreateDto.Response.of(response);
  }

  // 회원 전체 조회
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
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
  public MemberTotalInfoDto getMemberDetail(@PathVariable("memberId") Long memberId) {
    String memberCode = memberService.getMemberData(memberId).getMemberCode();

    return memberTotalInfoService.getMemberTotalInfo(memberCode);
  }

  // 회원 삭제
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{memberId}")
  public String deleteMember(@PathVariable("memberId") Long memberId) {
    return memberService.deleteMemberData(memberId);
  }

  // 회원 패스워드 초기화
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{memberId}/init-password")
  public String memberPasswordInit(@PathVariable("memberId") Long memberId) {
    return memberService.initMemberPassword(memberId);
  }
}
