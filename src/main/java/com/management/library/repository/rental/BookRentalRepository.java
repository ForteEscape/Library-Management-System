package com.management.library.repository.rental;

import com.management.library.domain.rental.Rental;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRentalRepository extends JpaRepository<Rental, Long>,
    BookRentalRepositoryCustom {

}
