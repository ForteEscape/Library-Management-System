package com.management.library.controller.member;

import com.management.library.controller.admin.dto.MemberRentalResponseDto;
import com.management.library.controller.dto.BookRentalSearchCond;
import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.RentalAllDto;
import com.management.library.controller.member.dto.MemberRentalOverviewDto;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalDurationExtendDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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

@Api(tags = {"회원 전용 도서 대여 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/member-info/rentals")
public class MemberRentalController {

  private final RentalService rentalService;

  // 회원 대여 기록 조회
  @PreAuthorize("hasRole('MEMBER')")
  @GetMapping
  @ApiOperation(value = "회원 대여 기록 조회", notes = "회원의 대여 기록들을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속한 회원 정보")
  })
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
  @ApiOperation(value = "회원 대여 기록 상세 조회", notes = "회원이 대여한 기록을 상세히 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "rentalId", value = "대여 id")
  })
  public MemberRentalResponseDto getMemberRentalDetail(@PathVariable("rentalId") Long rentalId) {
    return MemberRentalResponseDto.of(rentalService.getRentalDetail(rentalId));
  }

  //회원 대여 기간 연장
  @PreAuthorize("hasRole('MEMBER')")
  @PostMapping("/{rentalId}/extend-duration")
  @ApiOperation(value = "대여 기한 연장", notes = "진행 중인 대여의 기간을 7일 늘릴 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "name", value = "접속한 회원 정보"),
      @ApiImplicitParam(name = "rentalId", value = "대여 id"),
  })
  public RentalDurationExtendDto extendMemberRental(@PathVariable("rentalId") Long rentalId,
      Principal principal) {
    return rentalService.extendRentalDuration(principal.getName(), rentalId);
  }
}
