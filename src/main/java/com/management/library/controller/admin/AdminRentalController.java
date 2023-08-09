package com.management.library.controller.admin;

import com.management.library.controller.admin.dto.RentalRequestDto;
import com.management.library.controller.admin.dto.RentalResponseDto;
import com.management.library.controller.admin.dto.ReturnBookDataDto;
import com.management.library.controller.dto.AdminRentalControllerResponseDto;
import com.management.library.controller.dto.AdminRentalOverviewDto;
import com.management.library.controller.dto.BookRentalSearchCond;
import com.management.library.controller.dto.PageInfo;
import com.management.library.controller.dto.RentalAllDto;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.management.library.service.rental.dto.ReturnBookResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"관리자 전용 대여 기능 api"})
@ApiResponses({
    @ApiResponse(code = 200, message = "Success"),
    @ApiResponse(code = 400, message = "Bad Request"),
    @ApiResponse(code = 500, message = "Internal Server Error")
})
@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/rentals")
public class AdminRentalController {

  private final RentalService rentalService;

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  @ApiOperation(value = "도서 대여 생성", notes = "새로운 도서 대여 정보를 생성한다.")
  public RentalResponseDto createRental(
      @RequestBody @Valid RentalRequestDto request
  ) {
    RentalServiceResponseDto bookRental = rentalService.createBookRental(request.getMemberCode(),
        RentalBookInfoDto.of(request), LocalDate.now());

    return RentalResponseDto.of(bookRental);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  @ApiOperation(value = "도서 대여 내역 조회", notes = "도서 대여 내역을 조회할 수 있다.")
  public ResponseEntity<?> getRentalList(BookRentalSearchCond cond, Pageable pageable) {
    Page<RentalServiceResponseDto> resultPage = rentalService.getRentalData(cond, pageable);
    PageInfo pageInfo = new PageInfo(pageable.getPageNumber(), pageable.getPageSize(),
        (int) resultPage.getTotalElements(), resultPage.getTotalPages());

    List<AdminRentalOverviewDto> result = resultPage.getContent().stream()
        .map(AdminRentalOverviewDto::of)
        .collect(Collectors.toList());

    return new ResponseEntity<>(
        new RentalAllDto<>(result, pageInfo),
        HttpStatus.OK
    );
  }

  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{rentalId}")
  @ApiOperation(value = "도서 대여 내역 상세 조회", notes = "특정 대여 내역을 조회할 수 있다.")
  @ApiImplicitParams({
      @ApiImplicitParam(name = "rentalId", value = "대여 id"),
  })
  public AdminRentalControllerResponseDto getRentalDetail(@PathVariable("rentalId") Long rentalId) {
    RentalServiceResponseDto rentalDetail = rentalService.getRentalDetail(rentalId);

    return AdminRentalControllerResponseDto.of(rentalDetail);
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/return")
  @ApiOperation(value = "도서 반납", notes = "대여된 도서를 반납한다.")
  public ReturnBookResponseDto returnBook(@RequestBody ReturnBookDataDto request) {
    return rentalService.returnBook(request.getMemberCode(), request.getBookTitle(),
        request.getAuthor());
  }
}
