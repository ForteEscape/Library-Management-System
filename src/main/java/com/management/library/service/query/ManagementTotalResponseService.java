package com.management.library.service.query;

import com.management.library.service.query.dto.ManagementResultDto;
import com.management.library.service.query.dto.ManagementTotalResponseDto;
import com.management.library.service.request.management.ManagementService;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;
import com.management.library.service.result.management.ManagementResultService;
import com.management.library.service.result.management.dto.ManagementResultCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagementTotalResponseService {

  private final ManagementService managementService;
  private final ManagementResultService managementResultService;

  public ManagementTotalResponseDto getManagementTotalData(Long requestId){
    Response managementRequestDetail = managementService.getManagementRequestDetail(requestId);
    ManagementResultCreateDto.Response requestResult = managementResultService.getResultByRequestId(
        requestId);

    if (requestResult == null){
      return ManagementTotalResponseDto.of(managementRequestDetail, null);
    }
    ManagementResultDto result = ManagementResultDto.of(requestResult);

    return ManagementTotalResponseDto.of(managementRequestDetail, result);
  }
}
