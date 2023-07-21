package com.management.library.repository.management;

import com.management.library.domain.management.ManagementRequest;
import com.management.library.dto.RequestSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagementRequestRepositoryCustom {

  Page<ManagementRequest> findByMemberCode(String memberCode, Pageable pageable);

  Page<ManagementRequest> findAll(RequestSearchCond cond, Pageable pageable);
}
