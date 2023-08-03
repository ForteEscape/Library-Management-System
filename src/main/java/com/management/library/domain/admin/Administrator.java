package com.management.library.domain.admin;

import com.management.library.domain.BaseEntity;
import com.management.library.domain.type.Authority;
import com.management.library.service.admin.dto.AdminServiceCreateDto;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Administrator extends BaseEntity implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "administrator_id")
  private Long id;

  private String name;
  private String email;
  private String password;

  @Enumerated(EnumType.STRING)
  private Authority authority;

  @Builder
  private Administrator(Long id, String name, String email, String password, Authority authority) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.password = password;
    this.authority = authority;
  }

  public static Administrator of(AdminServiceCreateDto.Request request){
    return Administrator.builder()
        .name(request.getName())
        .email(request.getEmail())
        .password(request.getPassword())
        .authority(Authority.ROLE_ADMIN)
        .build();
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
