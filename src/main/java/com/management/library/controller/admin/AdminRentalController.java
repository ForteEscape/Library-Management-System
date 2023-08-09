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

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/rentals")
public class AdminRentalController {

  private final RentalService rentalService;

  // 도서 대여 생성
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping
  public RentalResponseDto createRental(
      @RequestBody @Valid RentalRequestDto request
  ) {
    RentalServiceResponseDto bookRental = rentalService.createBookRental(request.getMemberCode(),
        RentalBookInfoDto.of(request), LocalDate.now());

    return RentalResponseDto.of(bookRental);
  }

  // 도서 대여 내역 조회
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
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

  // 도서 대여 내역 상세 조회
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{rentalId}")
  public AdminRentalControllerResponseDto getRentalDetail(@PathVariable("rentalId") Long rentalId) {
    RentalServiceResponseDto rentalDetail = rentalService.getRentalDetail(rentalId);

    return AdminRentalControllerResponseDto.of(rentalDetail);
  }

  // 도서 반납
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/return")
  public ReturnBookResponseDto returnBook(@RequestBody ReturnBookDataDto request) {
    return rentalService.returnBook(request.getMemberCode(), request.getBookTitle(),
        request.getAuthor());
  }
}
