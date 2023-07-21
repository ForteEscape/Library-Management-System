package com.management.library.repository.newbook;

import com.management.library.domain.newbook.NewBookRequest;
import com.management.library.dto.RequestSearchCond;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewBookRequestRepositoryCustom {

  Page<NewBookRequest> findByMemberCode(String memberCode, Pageable pageable);

  Page<NewBookRequest> findAll(RequestSearchCond cond, Pageable pageable);
}
