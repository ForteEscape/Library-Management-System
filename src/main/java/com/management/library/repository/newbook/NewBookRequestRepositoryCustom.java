package com.management.library.repository.newbook;

import static com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;

import com.management.library.dto.RequestSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewBookRequestRepositoryCustom {

  Page<Response> findByMemberCode(String memberCode, Pageable pageable);

  Page<Response> findAll(RequestSearchCond cond, Pageable pageable);
}
