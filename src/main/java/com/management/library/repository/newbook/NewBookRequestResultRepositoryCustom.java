package com.management.library.repository.newbook;

import com.management.library.domain.newbook.NewBookRequestResult;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewBookRequestResultRepositoryCustom {

  Optional<NewBookRequestResult> findByRequestId(Long requestId);

  Page<NewBookRequestResult> findByAdminId(String adminEmail, Pageable pageable);
}
