package com.management.library.repository.newbook;

import static com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Response;

import com.management.library.domain.newbook.NewBookRequestResult;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NewBookRequestResultRepositoryCustom {

  Optional<NewBookRequestResult> findByRequestId(Long requestId);

  Page<Response> findByAdminId(String adminEmail, Pageable pageable);
}
