package com.management.library.service.member;

import static com.management.library.domain.type.Authority.ROLE_MEMBER;
import static com.management.library.exception.ErrorCode.DUPLICATE_MEMBER_CODE;
import static com.management.library.exception.ErrorCode.MEMBER_ALREADY_EXISTS;
import static com.management.library.exception.ErrorCode.MEMBER_NOT_EXISTS;

import com.management.library.domain.member.Address;
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
import com.management.library.service.rental.dto.RentalServiceResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final BookRentalRepository bookRentalRepository;
  private final Generator<String> memberPasswordGenerator;
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

    if (isMemberCodeDuplicate(memberCode)) {
      throw new DuplicateException(DUPLICATE_MEMBER_CODE);
    }

    String password = memberPasswordGenerator.generate(request.getBirthdayCode());

    Address address = getAddress(request.getLegion(), request.getCity(), request.getStreet());
    Member member = getMember(request.getName(), request.getBirthdayCode(), memberCode,
        password, address);

    Member savedMember = memberRepository.save(member);

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
   * 대여 가능 상태의 경우 redis 조회를 통해 가져온다.
   *
   * @param memberCode 회원 번호
   * @return 조회된 회원 DTO
   */
  public MemberReadServiceDto getMemberData(String memberCode) {
    Member member = memberRepository.findByMemberCode(memberCode)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    return MemberReadServiceDto.of(member);
  }

  /** 리펙토링 필요 -> 해당 메서드 rental service 로 이관해야함
   * 회원의 도서 대여 정보 기록 조회 redis cache 를 사용하여 앞으로 몇 권 더 빌릴 수 있는지를 확인할 수 있도록 하는 것도 괜찮을지도 redis Template
   * 관련 정보 모으기 필요
   *
   * @param cond       대여 상태 필터 데이터
   * @param memberCode 회원 번호
   * @param pageable   페이지 정보
   * @return 조회된 데이터 페이지
   */
  public Page<RentalServiceResponseDto> getMemberRentalData(
      BookRentalSearchCond cond, String memberCode, Pageable pageable) {

    return bookRentalRepository.findRentalPageByMemberCode(cond, memberCode, pageable);
  }

  private Address getAddress(String legion, String city, String street) {
    return Address.builder()
        .legion(legion)
        .city(city)
        .street(street)
        .build();
  }

  private Member getMember(String name, String birthdayCode, String memberCode, String password,
      Address address) {
    return Member.builder()
        .name(name)
        .birthdayCode(birthdayCode)
        .address(address)
        .authority(ROLE_MEMBER)
        .memberCode(memberCode)
        .password(password)
        .build();
  }

}
