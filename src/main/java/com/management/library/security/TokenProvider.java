package com.management.library.security;

import com.management.library.domain.type.Authority;
import com.management.library.service.admin.AdminService;
import com.management.library.service.member.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenProvider {

  private static final long TOKEN_EXPIRE_TIME = 1000 * 60 * 60;
  private static final String KEY_ROLES = "roles";
  private final MemberService memberService;
  private final AdminService adminService;

  @Value("{spring.jwt.secret}")
  private String secretKey;

  public String generateToken(String userName, Authority authority){
    Claims claims = Jwts.claims().setSubject(userName);
    claims.put(KEY_ROLES, authority);

    Date now = new Date();
    Date expireDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

    return Jwts.builder()
        .setClaims(claims)
        .setIssuedAt(now)
        .setExpiration(expireDate)
        .signWith(SignatureAlgorithm.HS512, secretKey)
        .compact();
  }

  public String getUserName(String token){
    return parseClaims(token).getSubject();
  }

  public boolean validateToken(String token){
    if (!StringUtils.hasText(token)) return false;

    Claims claims = parseClaims(token);

    return !claims.getExpiration().before(new Date());
  }

  private Claims parseClaims(String token) {
    try{
      return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
    } catch (ExpiredJwtException e){
      return e.getClaims();
    }
  }

  public Authentication getAuthenticationFromMember(String jwt){
    UserDetails userDetails = memberService.loadUserByUsername(getUserName(jwt));

    log.info("authorities = {}", userDetails.getAuthorities());

    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }

  public Authentication getAuthenticationFromAdmin(String jwt){
    UserDetails userDetails = adminService.loadUserByUsername(getUserName(jwt));

    log.info("authorities = {}", userDetails.getAuthorities());

    return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
  }
}
