package com.management.library.repository.rental;

import com.management.library.domain.rental.Rental;
import com.management.library.domain.type.RentalStatus;
import com.management.library.dto.BookRentalSearchCond;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRentalRepositoryCustom {

  Page<Rental> findRentalPageByMemberCode(String memberCode, Pageable pageable);

  List<Rental> findRentalListByMemberCode(String memberCode);

  Page<Rental> findAllWithPage(BookRentalSearchCond cond, Pageable pageable);
}
