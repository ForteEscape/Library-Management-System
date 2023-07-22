package com.management.library.repository.management;

import com.management.library.domain.management.ManagementRequestResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagementRequestResultRepository extends
    JpaRepository<ManagementRequestResult, Long>, ManagementRequestResultRepositoryCustom {

}
