package com.management.library.repository.management;

import static com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;

import com.management.library.domain.management.ManagementRequest;
import com.management.library.dto.RequestSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ManagementRequestRepositoryCustom {

  Page<Response> findByMemberCode(String memberCode, Pageable pageable);

  Page<ManagementRequest> findAll(RequestSearchCond cond, Pageable pageable);
}
