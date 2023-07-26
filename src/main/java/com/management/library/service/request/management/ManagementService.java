package com.management.library.service.request.management;

import static com.management.library.exception.ErrorCode.MEMBER_NOT_EXISTS;
import static com.management.library.service.request.management.dto.ManagementRequestServiceDto.Request;
import static com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;

import com.management.library.domain.management.ManagementRequest;
import com.management.library.domain.member.Member;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.management.ManagementRequestRepository;
import com.management.library.repository.member.MemberRepository;
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
  private final RedisManagementRequestService redisManagementRequestService;
  private final MemberRepository memberRepository;

  // 운영 개선 사항 요청 등록
  @Transactional
  public Response createManagementRequest(Request request, String memberCode) {
    Member member = memberRepository.findByMemberCode(memberCode)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    redisManagementRequestService.checkRequestCount(memberCode);

    ManagementRequest savedMember = managementRequestRepository.save(
        ManagementRequest.of(request, member));

    return Response.of(savedMember);
  }

  // 운영 개선 사항을 회원의 이메일로 찾아 반환한다.
  public Page<Response> getMemberManagementRequest(String memberCode, Pageable pageable){
    return managementRequestRepository.findByMemberCode(memberCode, pageable);
  }
}
