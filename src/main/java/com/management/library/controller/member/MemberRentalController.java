package com.management.library.controller.member;

import com.management.library.controller.admin.dto.MemberRentalResponseDto;
import com.management.library.controller.dto.BookRentalSearchCond;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalDurationExtendDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member-info/rentals")
public class MemberRentalController {

  private final RentalService rentalService;

  // 회원 대여 기록 조회
  @GetMapping
  public Page<RentalServiceResponseDto> getMemberRentalData(BookRentalSearchCond cond,
      Pageable pageable, Principal principal) {

    return rentalService.getMemberRentalData(cond, principal.getName(), pageable);
  }

  // 회원 대여 기록 상세 조회
  @GetMapping("/{rentalId}")
  public MemberRentalResponseDto getMemberRentalDetail(@PathVariable("rentalId") Long rentalId) {
    return MemberRentalResponseDto.of(rentalService.getRentalDetail(rentalId));
  }

  //회원 대여 기간 연장
  @PostMapping("/{rentalId}/extend-duration")
  public RentalDurationExtendDto extendMemberRental(@PathVariable("rentalId") Long rentalId,
      Principal principal) {
    return rentalService.extendRentalDuration(principal.getName(), rentalId);
  }
}
