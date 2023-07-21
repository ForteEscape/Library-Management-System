package com.management.library.repository.newbook;

import com.management.library.domain.newbook.NewBookRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewBookRequestRepository extends JpaRepository<NewBookRequest, Long>,
    NewBookRequestRepositoryCustom {

}
