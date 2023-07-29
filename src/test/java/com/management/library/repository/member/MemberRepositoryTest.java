package com.management.library.repository.member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.type.Authority;
import java.util.List;
import java.util.NoSuchElementException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class MemberRepositoryTest {

  @Autowired
  private MemberRepository memberRepository;

  @DisplayName("회원을 회원 도서 번호로 조회할 수 있다.")
  @Test
  public void findByMemberCode() {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("park", "123457");
    Member member3 = createMember("lee", "123458");

    memberRepository.saveAll(List.of(member1, member2, member3));

    // when
    Member member = memberRepository.findByMemberCode("123456")
        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다."));

    // then
    assertThat(member)
        .extracting("name", "memberCode")
        .containsExactlyInAnyOrder(
            "kim", "123456"
        );
  }

  @DisplayName("회원을 회원 도서 번호로 조회할 수 있다. 존재하지 않는 경우 예외를 발생시킨다.")
  @Test
  public void findByMemberCodeWithNoMember() {
    // when
    // then
    assertThatThrownBy(() -> memberRepository.findByMemberCode("123456")
        .orElseThrow(() -> new IllegalArgumentException("사용자가 존재하지 않습니다.")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("사용자가 존재하지 않습니다.");
  }

  @DisplayName("특정 회원의 이름과 주소로 회원을 조회할 수 있다.")
  @Test
  public void findByMemberNameAndAddress() {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("park", "123457");
    Member member3 = createMember("lee", "123458");
    Member member4 = createMember("kim", "123459");
    Member member5 = createMember("im", "123460");
    Member member6 = createMember("han", "123461");
    Member member7 = createMember("hong", "123462");
    Member member8 = createMember("kang", "123463");

    memberRepository.saveAll(
        List.of(member1, member2, member3, member4, member5, member6, member7, member8)
    );

    // when
    Member member = memberRepository.findByMemberNameAndAddress("park", "경상남도", "김해시", "삼계로")
        .orElseThrow(() -> new NoSuchElementException("해당 회원이 존재하지 않습니다."));

    // then
    assertThat(member)
        .extracting("name", "memberCode")
        .contains("park", "123457");

    assertThat(member)
        .extracting(Member::getAddress)
        .extracting(Address::getLegion, Address::getCity, Address::getStreet)
        .contains("경상남도", "김해시", "삼계로");
  }

  @DisplayName("존재하지 않는 회원을 조회시 예외를 발생시킨다.")
  @Test
  public void findByMemberNameAndAddressWithNoResult() {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("park", "123457");
    Member member3 = createMember("lee", "123458");
    Member member4 = createMember("kim", "123459");
    Member member5 = createMember("im", "123460");
    Member member6 = createMember("han", "123461");
    Member member7 = createMember("hong", "123462");
    Member member8 = createMember("kang", "123463");

    memberRepository.saveAll(
        List.of(member1, member2, member3, member4, member5, member6, member7, member8)
    );

    // when
    // then
    assertThatThrownBy(() -> memberRepository.findByMemberNameAndAddress("um", "경상남도", "김해시", "삼계로")
        .orElseThrow(() -> new NoSuchElementException("해당 회원이 존재하지 않습니다.")))
        .isInstanceOf(NoSuchElementException.class)
        .hasMessage("해당 회원이 존재하지 않습니다.");
  }

  private static Member createMember(String name, String memberCode) {
    Address address = Address.builder()
        .legion("경상남도")
        .city("김해시")
        .street("삼계로")
        .build();

    return Member.builder()
        .name(name)
        .birthdayCode("980101")
        .memberCode(memberCode)
        .address(address)
        .password("1234")
        .authority(Authority.ROLE_MEMBER)
        .build();
  }
}