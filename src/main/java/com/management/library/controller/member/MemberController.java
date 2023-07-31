package com.management.library.controller.member;

import com.management.library.controller.admin.dto.MemberRentalDto;
import com.management.library.controller.member.dto.MemberUpdateControllerDto;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.dto.MemberUpdateServiceDto;
import com.management.library.service.member.MemberService;
import com.management.library.service.query.ManagementTotalResponseService;
import com.management.library.service.query.MemberTotalInfoService;
import com.management.library.service.query.NewBookTotalResponseService;
import com.management.library.service.query.dto.ManagementTotalResponseDto;
import com.management.library.service.query.dto.MemberTotalInfoDto;
import com.management.library.service.query.dto.NewBookTotalResponseDto;
import com.management.library.service.query.dto.PasswordChangeDto;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalDurationExtendDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  private final RentalService rentalService;
  private final NewBookService newBookService;
  private final ManagementService managementService;
  private final ManagementTotalResponseService managementTotalResponseService;
  private final NewBookTotalResponseService newBookTotalResponseService;
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
  public void changeMemberPassword(@RequestBody PasswordChangeDto request, Principal principal) {

  }

  // 회원 대여 기록 조회
  @GetMapping("/rentals")
  public Page<RentalServiceResponseDto> getMemberRentalData(BookRentalSearchCond cond,
      Pageable pageable, Principal principal) {

    return rentalService.getMemberRentalData(cond, principal.getName(), pageable);
  }

  // 회원 대여 기록 상세 조회
  @GetMapping("/rentals/{rentalId}")
  public MemberRentalDto getMemberRentalDetail(@PathVariable("rentalId") Long rentalId) {
    return MemberRentalDto.of(rentalService.getMemberRentalDetail(rentalId));
  }

  //회원 대여 기간 연장
  @PostMapping("/rentals/{rentalId}/extend-duration")
  public RentalDurationExtendDto extendMemberRental(@PathVariable("rentalId") Long rentalId,
      Principal principal) {
    return rentalService.extendRentalDuration(principal.getName(), rentalId);
  }

  // 회원 신간 요청 기록 조회
  @GetMapping("/new-book-requests")
  public Page<NewBookRequestServiceDto.Response> getMemberNewBookRequest(Principal principal,
      Pageable pageable) {
    return newBookService.getMemberNewBookRequest(principal.getName(), pageable);
  }

  @GetMapping("/new-book-requests/{requestId}")
  public NewBookTotalResponseDto getMemberNewBookRequestDetail(
      @PathVariable("requestId") Long requestId) {
    return newBookTotalResponseService.getNewBookTotalResponse(requestId);
  }

  // 회원 운영 개선 요청 기록 조회
  @GetMapping("/management-requests")
  public Page<ManagementRequestServiceDto.Response> getMemberManagementRequest(Principal principal,
      Pageable pageable) {
    return managementService.getMemberManagementRequest(principal.getName(), pageable);
  }

  @GetMapping("/management-requests/{requestId}")
  public ManagementTotalResponseDto getMemberManagementRequestDetail(
      @PathVariable("requestId") Long requestId) {
    return managementTotalResponseService.getManagementTotalData(requestId);
  }
}
