package com.management.library.controller.admin;

import com.management.library.controller.admin.dto.RentalRequestDto;
import com.management.library.controller.admin.dto.RentalResponseDto;
import com.management.library.controller.admin.dto.ReturnBookDataDto;
import com.management.library.controller.dto.BookRentalSearchCond;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalBookInfoDto;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import com.management.library.service.rental.dto.ReturnBookResponseDto;
import java.security.Principal;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
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
  @PostMapping
  public RentalResponseDto createRental(
      @RequestBody RentalRequestDto request,
      Principal principal
  ) {
    RentalServiceResponseDto bookRental = rentalService.createBookRental(principal.getName(),
        RentalBookInfoDto.of(request), LocalDate.now());

    return RentalResponseDto.of(bookRental);
  }

  // 도서 대여 내역 조회
  @GetMapping
  public Page<RentalServiceResponseDto> getRentalList(BookRentalSearchCond cond,
      Pageable pageable) {

    return rentalService.getRentalData(cond, pageable);
  }

  // 도서 반납
  @PostMapping("/return")
  public ReturnBookResponseDto returnBook(@RequestBody ReturnBookDataDto request) {
    return rentalService.returnBook(request.getMemberCode(), request.getBookTitle(),
        request.getAuthor());
  }
}
