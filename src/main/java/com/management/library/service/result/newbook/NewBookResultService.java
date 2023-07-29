package com.management.library.service.result.newbook;

import static com.management.library.exception.ErrorCode.ADMIN_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.REQUEST_NOT_EXISTS;
import static com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Request;
import static com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Response;

import com.management.library.domain.admin.Administrator;
import com.management.library.domain.newbook.NewBookRequest;
import com.management.library.domain.newbook.NewBookRequestResult;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.repository.newbook.NewBookRequestRepository;
import com.management.library.repository.newbook.NewBookRequestResultRepository;
import com.management.library.service.request.RedisRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewBookResultService {

  private final NewBookRequestRepository newBookRequestRepository;
  private final NewBookRequestResultRepository newBookRequestResultRepository;
  private final AdministratorRepository administratorRepository;
  private final RedisRequestService redisRequestService;

  @Transactional
  public Response createResult(Request request, Long newBookRequestId, String adminEmail) {
    NewBookRequest newBookRequest = newBookRequestRepository.findById(newBookRequestId)
        .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));

    Administrator administrator = administratorRepository.findByEmail(adminEmail)
        .orElseThrow(() -> new NoSuchElementExistsException(ADMIN_NOT_EXISTS));

    redisRequestService.removeBookRequestCache(newBookRequestId);

    newBookRequest.changeRequestStatus(request.getResultStatus());

    return Response.of(
        newBookRequestResultRepository.save(
            NewBookRequestResult.of(request, newBookRequest, administrator)
        )
    );
  }

  public Page<Response> getResultByAdminEmail(String adminEmail, Pageable pageable){
    return newBookRequestResultRepository.findByAdminId(adminEmail, pageable);
  }
}
