package com.management.library.repository.management;

import com.management.library.domain.management.ManagementRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ManagementRequestRepository extends JpaRepository<ManagementRequest, Long>,
    ManagementRequestRepositoryCustom {

}
