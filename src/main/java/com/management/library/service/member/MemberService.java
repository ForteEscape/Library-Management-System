package com.management.library.service.member;

import static com.management.library.exception.ErrorCode.DUPLICATE_MEMBER_CODE;
import static com.management.library.exception.ErrorCode.MEMBER_ALREADY_EXISTS;
import static com.management.library.exception.ErrorCode.MEMBER_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.PASSWORD_NOT_MATCH;

import com.management.library.controller.admin.dto.MemberSearchCond;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.service.member.dto.MemberUpdateServiceDto;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.InvalidAccessException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.Generator;
import com.management.library.service.member.dto.MemberCreateServiceDto;
import com.management.library.service.member.dto.MemberCreateServiceDto.Request;
import com.management.library.service.member.dto.MemberCreateServiceDto.Response;
import com.management.library.service.member.dto.MemberReadServiceDto;
import com.management.library.service.query.dto.PasswordChangeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {

  private final MemberRepository memberRepository;
  private final Generator<String> memberPasswordGenerator;
  private final RedisMemberService redisService;
  private final PasswordEncoder passwordEncoder;

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
    String encodedPassword = passwordEncoder.encode(password);

    Address address = Address.of(request);
    Member member = Member.of(request, memberCode, encodedPassword, address);

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

  public MemberReadServiceDto getMemberData(Long id){
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    return MemberReadServiceDto.of(member);
  }

  public Page<MemberReadServiceDto> getMemberDataList(MemberSearchCond cond, Pageable pageable){
    return memberRepository.findAll(cond, pageable);
  }

  @Transactional
  public String deleteMemberData(Long memberId){
    memberRepository.deleteById(memberId);

    return "success";
  }

  @Transactional
  public String initMemberPassword(Long id){
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    String initPassword = memberPasswordGenerator.generate(member.getBirthdayCode());

    // 암호화해서 넘겨줘야함
    member.changePassword(passwordEncoder.encode(initPassword));

    return initPassword;
  }

  @Transactional
  public String changePassword(String memberCode, PasswordChangeDto request){
    Member member = memberRepository.findByMemberCode(memberCode)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())){
      throw new InvalidAccessException(PASSWORD_NOT_MATCH);
    }

    member.changePassword(passwordEncoder.encode(request.getNewPassword()));
    return "success";
  }

  @Transactional
  public String updateMemberData(MemberUpdateServiceDto request, String memberCode) {
    Member member = memberRepository.findByMemberCode(memberCode)
        .orElseThrow(() -> new NoSuchElementExistsException(MEMBER_NOT_EXISTS));

    member.changeMemberData(request);

    return "success";
  }
}
