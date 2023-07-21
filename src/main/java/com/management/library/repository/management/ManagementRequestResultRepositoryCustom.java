package com.management.library.repository.management;

import com.management.library.domain.management.ManagementRequestResult;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagementRequestResultRepositoryCustom {

  Optional<ManagementRequestResult> findByRequestId(Long requestId);

  Page<ManagementRequestResult> findByAdminEmail(String adminEmail, Pageable pageable);
}
