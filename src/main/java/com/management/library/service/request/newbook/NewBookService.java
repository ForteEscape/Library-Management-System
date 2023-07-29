package com.management.library.service.request.newbook;

import static com.management.library.exception.ErrorCode.MEMBER_NOT_EXISTS;
import static com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Request;
import static com.management.library.service.request.newbook.dto.NewBookRequestServiceDto.Response;

import com.management.library.domain.member.Member;
import com.management.library.domain.newbook.NewBookRequest;
import com.management.library.dto.RequestSearchCond;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.newbook.NewBookRequestRepository;
import com.management.library.service.request.RedisRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewBookService {

  private final NewBookRequestRepository newBookRequestRepository;
  private final RedisRequestService redisRequestService;
  private final MemberRepository memberRepository;

  @Transactional
  public Response createNewBookRequest(Request request, String memberCode) {
    Member member = memberRepository.findByMemberCode(memberCode)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    redisRequestService.checkNewBookRequestCount(memberCode);

    NewBookRequest savedRequest = newBookRequestRepository.save(NewBookRequest.of(request, member));

    redisRequestService.addBookRequestCache(savedRequest.getId());

    return Response.of(savedRequest);
  }

  public Page<Response> getMemberNewBookRequest(String memberCode, Pageable pageable){
    return newBookRequestRepository.findByMemberCode(memberCode, pageable);
  }

  public Page<Response> getAllNewBookRequest(RequestSearchCond cond, Pageable pageable){
    return newBookRequestRepository.findAll(cond, pageable);
  }
}
