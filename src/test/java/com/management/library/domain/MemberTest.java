package com.management.library.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.dto.MemberUpdateServiceDto;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@Slf4j
class MemberTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("회원 엔티티 테스트")
  void memberEntityTest() {
    // given
    Member member = Member.builder()
        .name("memberA")
        .password("1234")
        .birthdayCode("980506")
        .address(Address.of("서울", "강변", "강변길"))
        .memberCode("123456")
        .build();

    em.persist(member);

    em.flush();
    em.clear();

    // when
    Member result = em.find(Member.class, member.getId());

    // then
    assertThat(result.getName()).isEqualTo(member.getName());
    assertThat(result.getPassword()).isEqualTo(member.getPassword());
    assertThat(result.getBirthdayCode()).isEqualTo(member.getBirthdayCode());
    assertThat(result.getMemberCode()).isEqualTo(member.getMemberCode());
  }

  @Test
  @DisplayName("회원 엔티티 변경 - 패스워드 변경")
  void changeMemberPassword() {
    // given
    Member member = Member.builder()
        .name("memberA")
        .password("1234")
        .birthdayCode("980506")
        .address(Address.of("서울", "강변", "강변길"))
        .memberCode("123456")
        .build();

    em.persist(member);

    em.flush();
    em.clear();

    // when
    Member result = em.find(Member.class, member.getId());
    result.changePassword("5678");

    em.flush();
    em.clear();

    Member changePasswordEntity = em.find(Member.class, member.getId());

    // when
    assertThat(changePasswordEntity.getPassword()).isEqualTo("5678");
  }

  @Test
  @DisplayName("회원 엔티티 변경 테스트 - 회원 정보(이름, 거주 주소)")
  void changeMemberDateTest(){
    // given
    Member member = Member.builder()
        .name("memberA")
        .password("1234")
        .birthdayCode("980506")
        .address(Address.of("서울", "강변", "강변길"))
        .memberCode("123456")
        .build();

    em.persist(member);

    em.flush();
    em.clear();

    MemberUpdateServiceDto request = MemberUpdateServiceDto.builder()
        .name("memberB")
        .legion("부산")
        .city("사상구")
        .street("사상로")
        .build();

    // when
    Member result = em.find(Member.class, member.getId());
    result.changeMemberData(request);

    em.flush();
    em.clear();

    Member changeMemberInfoEntity = em.find(Member.class, member.getId());

    // then
    assertThat(changeMemberInfoEntity.getName()).isEqualTo("memberB");
    assertThat(changeMemberInfoEntity.getAddress().getLegion()).isEqualTo("부산");
    assertThat(changeMemberInfoEntity.getAddress().getCity()).isEqualTo("사상구");
    assertThat(changeMemberInfoEntity.getAddress().getStreet()).isEqualTo("사상로");
  }
}