package com.management.library.controller.member;

import com.management.library.controller.admin.dto.MemberRentalResponseDto;
import com.management.library.controller.dto.BookRentalSearchCond;
import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.RentalAllDto;
import com.management.library.controller.member.dto.MemberRentalOverviewDto;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalDurationExtendDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
  @PreAuthorize("hasRole('MEMBER')")
  @GetMapping
  public ResponseEntity<?> getMemberRentalData(BookRentalSearchCond cond, Pageable pageable,
      Principal principal) {

    Page<RentalServiceResponseDto> resultPage = rentalService.getMemberRentalData(cond,
        principal.getName(), pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<MemberRentalOverviewDto> result = resultPage.getContent().stream()
        .map(MemberRentalOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new RentalAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  // 회원 대여 기록 상세 조회
  @PreAuthorize("hasRole('MEMBER')")
  @GetMapping("/{rentalId}")
  public MemberRentalResponseDto getMemberRentalDetail(@PathVariable("rentalId") Long rentalId) {
    return MemberRentalResponseDto.of(rentalService.getRentalDetail(rentalId));
  }

  //회원 대여 기간 연장
  @PreAuthorize("hasRole('MEMBER')")
  @PostMapping("/{rentalId}/extend-duration")
  public RentalDurationExtendDto extendMemberRental(@PathVariable("rentalId") Long rentalId,
      Principal principal) {
    return rentalService.extendRentalDuration(principal.getName(), rentalId);
  }
}
