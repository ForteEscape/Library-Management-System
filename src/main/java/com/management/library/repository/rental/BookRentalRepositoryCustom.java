package com.management.library.repository.rental;

import com.management.library.domain.rental.Rental;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.service.rental.dto.RentalServiceReadDto;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRentalRepositoryCustom {

  Page<RentalServiceReadDto> findRentalPageByMemberCode(BookRentalSearchCond cond,
      String memberCode, Pageable pageable);

  List<Rental> findRentalListByMemberCode(String memberCode);

  Page<Rental> findAllWithPage(BookRentalSearchCond cond, Pageable pageable);
}
