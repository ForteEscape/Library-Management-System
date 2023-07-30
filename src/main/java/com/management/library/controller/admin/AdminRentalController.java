package com.management.library.controller.admin;

import com.management.library.dto.BookRentalSearchCond;
import com.management.library.service.rental.RentalService;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admins/rentals")
public class AdminRentalController {

  private final RentalService rentalService;

  @GetMapping
  public Page<RentalServiceResponseDto> getRentalList(BookRentalSearchCond cond,
      Pageable pageable) {

    return rentalService.getRentalData(cond, pageable);
  }
}
