package com.management.library.service.member;

import static com.management.library.exception.ErrorCode.DUPLICATE_MEMBER_CODE;
import static com.management.library.exception.ErrorCode.MEMBER_ALREADY_EXISTS;
import static com.management.library.exception.ErrorCode.MEMBER_NOT_EXISTS;

import com.management.library.domain.member.Member;
import com.management.library.dto.BookRentalSearchCond;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.member.MemberRepository;
import com.management.library.repository.rental.BookRentalRepository;
import com.management.library.service.Generator;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.member.dto.MemberCreateServiceDto.Request;
import com.management.library.service.member.dto.MemberCreateServiceDto.Response;
import com.management.library.service.member.dto.MemberReadServiceDto;
import com.management.library.service.rental.dto.RentalServiceReadDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

  private final MemberRepository memberRepository;
  private final RedissonClient redissonClient;
  private final BookRentalRepository bookRentalRepository;
  private final Generator<String> memberPasswordGenerator;
  private final MemberCreateService memberCreateService;
  private final RedisMemberService redisService;

  /**
   * 회원 가입 기능 회원의 이름 및 주소가 모두 동일한 경우 -> 일반적으로 동일인이라고 가정할 수 있으므로 중복으로 판단해 가입 제한. 회원 번호와 관련된 동시성 문제 발생
   * -> redis lock 을 사용하여 해결
   *
   * @param request 회원 가입 요청 DTO
   * @return 회원 가입 완료된 회원의 결과 DTO
   */
  @Transactional
  public MemberCreateServiceDto.Response createMember(MemberCreateServiceDto.Request request) {
    String memberCode = String.valueOf(redisService.getMemberCode());

    if (isMemberPresent(request)) {
      throw new DuplicateException(MEMBER_ALREADY_EXISTS);
    }

    if(isMemberCodeDuplicate(memberCode)){
      throw new DuplicateException(DUPLICATE_MEMBER_CODE);
    }
    String password = memberPasswordGenerator.generate(request.getBirthdayCode());

    Member savedMember = memberCreateService.saveMember(memberCode, password, request);
    Response response = Response.of(savedMember);
    response.setPassword(password);

    return response;
  }

  private boolean isMemberCodeDuplicate(String memberCode) {
    return memberRepository.findByMemberCode(memberCode).isPresent();
  }

  private boolean isMemberPresent(Request request) {
    return memberRepository.findByMemberNameAndAddress(request.getName(),
        request.getLegion(), request.getCity(), request.getStreet()).isPresent();
  }

  /**
   * 회원 마이페이지 조회 기능. 회원의 이름, 회원 번호 및 회원의 현재 대여 가능 상태를 반환한다.
   *
   * @param memberCode 회원 번호
   * @return 조회된 회원 DTO
   */
  public MemberReadServiceDto getMemberData(String memberCode) {
    Member member = memberRepository.findByMemberCode(memberCode)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    return MemberReadServiceDto.of(member);
  }

  /**
   * 회원의 도서 대여 정보 기록 조회 redis cache 를 사용하여 앞으로 몇 권 더 빌릴 수 있는지를 확인할 수 있도록 하는 것도 괜찮을지도 redis Template
   * 관련 정보 모으기 필요
   *
   * @param cond       대여 상태 필터 데이터
   * @param memberCode 회원 번호
   * @param pageable   페이지 정보
   * @return 조회된 데이터 페이지
   */
  public Page<RentalServiceReadDto> getMemberRentalData(
      BookRentalSearchCond cond, String memberCode, Pageable pageable) {

    return bookRentalRepository.findRentalPageByMemberCode(
        cond, memberCode, pageable);
  }

  // 회원이 요청한 신간 반입 요청 목록 데이터를 가져오기
  // 이건 그냥 각 requestService에서 수행하도록 한다.

}
