package com.management.library.repository.rental;

import com.management.library.controller.dto.BookRentalSearchCond;
import com.management.library.domain.rental.Rental;
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRentalRepositoryCustom {

  Page<RentalServiceResponseDto> findRentalPageByMemberCode(BookRentalSearchCond cond,
      String memberCode, Pageable pageable);

  List<RentalServiceResponseDto> findRentalListByMemberCode(String memberCode);

  Page<RentalServiceResponseDto> findAllWithPage(BookRentalSearchCond cond, Pageable pageable);

  Optional<Rental> findByBookInfoAndStatus(String memberCode, String bookTitle, String author);

  Optional<Rental> findByIdWithRental(Long rentalId);

  Optional<Rental> findByMemberCodeAndBookTitle(String memberCode, String bookTitle);

  Long countByRentalByDate(LocalDate startDate, LocalDate endDate);
}
