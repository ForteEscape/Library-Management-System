package com.management.library.service.result.management;

import static com.management.library.service.result.management.dto.ManagementResultCreateDto.Request;
import static com.management.library.service.result.management.dto.ManagementResultCreateDto.Response;

import com.management.library.domain.admin.Administrator;
import com.management.library.domain.management.ManagementRequest;
import com.management.library.domain.management.ManagementRequestResult;
import com.management.library.exception.ErrorCode;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.repository.management.ManagementRequestRepository;
import com.management.library.repository.management.ManagementRequestResultRepository;
import com.management.library.service.request.RedisRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagementResultService {

  private final ManagementRequestResultRepository resultRepository;
  private final ManagementRequestRepository requestRepository;
  private final AdministratorRepository administratorRepository;
  private final RedisRequestService redisRequestService;

  @Transactional
  public Response createResult(Request request, Long managementId, String adminEmail) {
    ManagementRequest managementRequest = requestRepository.findById(managementId)
        .orElseThrow(() -> new NoSuchElementExistsException(ErrorCode.REQUEST_NOT_EXISTS));

    Administrator administrator = administratorRepository.findByEmail(adminEmail)
        .orElseThrow(() -> new NoSuchElementExistsException(ErrorCode.ADMIN_NOT_EXISTS));

    // 답변이 작성되었으므로 캐시에서 제거 -> 앞으로 다른 관리자가 해당 요청에 대해 답변 작성 시 예외 발생
    redisRequestService.removeManagementRequestCache(managementId);

    // 답변 결과에 따른 원 요청의 요청 결과가 변경되어야 한다.
    managementRequest.changeRequestStatus(request.getResultStatus());

    return Response.of(resultRepository.save(
        ManagementRequestResult.of(request, managementRequest, administrator)));
  }

  // 관리자가 자신이 작성한 결과를 조회
  public Page<Response> getResultByAdminEmail(String adminEmail, Pageable pageable){
    return resultRepository.findByAdminEmail(adminEmail, pageable);
  }

  public Response getResultByRequestId(Long requestId){
    ManagementRequestResult managementRequestResult = resultRepository.findByRequestId(requestId)
        .orElse(null);

    if (managementRequestResult == null){
      return null;
    }

    return Response.of(managementRequestResult);
  }
}
