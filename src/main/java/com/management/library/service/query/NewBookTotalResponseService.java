package com.management.library.service.query;

import com.management.library.service.query.dto.NewBookResultDto;
import com.management.library.service.query.dto.NewBookTotalResponseDto;
import com.management.library.service.request.newbook.NewBookService;
import com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;
import com.management.library.service.result.newbook.NewBookResultService;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewBookTotalResponseService {

  private final NewBookService newBookService;
  private final NewBookResultService newBookResultService;

  public NewBookTotalResponseDto getNewBookTotalResponse(Long requestId){
    Response requestDetail = newBookService.getNewBookRequestDetail(requestId);
    NewBookResultCreateDto.Response result = newBookResultService.getResultByRequestId(
        requestId);

    if (result == null){
      return NewBookTotalResponseDto.of(requestDetail, null);
    }

    return NewBookTotalResponseDto.of(requestDetail, NewBookResultDto.of(result));
  }
}
