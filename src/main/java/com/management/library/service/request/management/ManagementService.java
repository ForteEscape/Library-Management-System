package com.management.library.service.request.management;

import static com.management.library.exception.ErrorCode.MEMBER_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.REQUEST_NOT_EXISTS;
import static com.management.library.service.request.management.dto.ManagementRequestServiceDto.Request;
import static com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;

import com.management.library.domain.management.ManagementRequest;
import com.management.library.domain.member.Member;
import com.management.library.dto.RequestSearchCond;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.management.ManagementRequestRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.request.RedisRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ManagementService {

  private final ManagementRequestRepository managementRequestRepository;
  private final RedisRequestService redisManagementRequestService;
  private final MemberRepository memberRepository;

  // 운영 개선 사항 요청 등록
  @Transactional
  public Response createManagementRequest(Request request, String memberCode) {
    Member member = memberRepository.findByMemberCode(memberCode)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    redisManagementRequestService.checkManagementRequestCount(memberCode);

    ManagementRequest savedRequest = managementRequestRepository.save(
        ManagementRequest.of(request, member));

    redisManagementRequestService.addManagementRequestCache(savedRequest.getId());

    return Response.of(savedRequest);
  }

  // 운영 개선 사항을 회원의 이메일로 찾아 반환한다.
  public Page<Response> getMemberManagementRequest(String memberCode, Pageable pageable){
    return managementRequestRepository.findByMemberCode(memberCode, pageable);
  }

  // 관리자가 현재 올라온 모든 운영 개선 요청 사항을 조회할 수 있고, 요청 상태에 따라 필터링 할 수 있다.
  public Page<Response> getAllManagementRequest(RequestSearchCond cond, Pageable pageable){
    return managementRequestRepository.findAll(cond, pageable);
  }

  public Response getManagementRequestDetail(Long id){
    ManagementRequest request = managementRequestRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementExistsException(REQUEST_NOT_EXISTS));

    return Response.of(request);
  }
}
