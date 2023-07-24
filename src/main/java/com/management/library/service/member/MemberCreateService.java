package com.management.library.service.member;

import static com.management.library.domain.type.Authority.ROLE_MEMBER;
import static com.management.library.domain.type.MemberRentalStatus.RENTAL_AVAILABLE;
import static com.management.library.service.member.dto.MemberCreateServiceDto.*;

import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class MemberCreateService {

  private final MemberRepository memberRepository;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public Member saveMember(String memberCode, String password, Request request){
    Address address = getAddress(request.getLegion(), request.getCity(), request.getStreet());
    Member member = getMember(request.getName(), request.getBirthdayCode(), memberCode,
        password, address);

    return memberRepository.save(member);
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
        .memberRentalStatus(RENTAL_AVAILABLE)
        .authority(ROLE_MEMBER)
        .memberCode(memberCode)
        .password(password)
        .build();
  }
}
