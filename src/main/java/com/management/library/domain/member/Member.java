package com.management.library.domain.member;

import com.management.library.domain.BaseEntity;
import com.management.library.domain.type.Authority;
import com.management.library.dto.MemberUpdateDto;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Table(indexes = @Index(name = "idx__member_code", columnList = "member_code"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "member_id")
  private Long id;

  private String name;
  private String password;
  private String birthdayCode;

  @Embedded
  private Address address;
  @Column(name = "member_code", unique = true)
  private String memberCode;

  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Builder
  public Member(Long id, String name, String password, String birthdayCode, Address address,
      String memberCode, Authority authority) {
    this.id = id;
    this.name = name;
    this.password = password;
    this.birthdayCode = birthdayCode;
    this.address = address;
    this.memberCode = memberCode;
    this.authority = authority;
  }

  /**
   * 회원 엔티티 데이터 변경
   *
   * @param request 변경 정보 DTO
   */
  public void changeMemberData(MemberUpdateDto.Request request) {
    this.name = request.getName();
    this.address = new Address(request.getLegion(), request.getCity(), request.getStreet());
  }

  /**
   * 비밀번호는 기본적으로 해싱을 수행한 값을 저장합니다. 엔티티 클래스는 이러한 해싱 관련 기술 종속성을 제거하기 위해 service 에서 해싱된 값을 받아 수정하기만 하도록
   * 수행합니다.
   *
   * @param password 변경할 패스워드
   */
  public void changePassword(String password) {
    this.password = password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  @Override
  public String getUsername() {
    return null;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return false;
  }
}
