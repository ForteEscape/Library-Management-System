package com.management.library.repository.newbook;

import com.management.library.domain.newbook.NewBookRequestResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewBookRequestResultRepository extends JpaRepository<NewBookRequestResult, Long>,
    NewBookRequestResultRepositoryCustom {

}
