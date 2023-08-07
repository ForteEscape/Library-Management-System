package com.management.library.security;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  public static final String TOKEN_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  private final TokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {

    String token = resolveTokenFromRequest(request);
    String requestURI = request.getRequestURI();

    if (StringUtils.hasText(token) && tokenProvider.validateToken(token)){
      Authentication auth;

      // 관리자 url을 타고 왔다면 관리자 전용으로 권한 인증
      if (requestURI.contains("/admins")){
        auth = tokenProvider.getAuthenticationFromAdmin(token);
      } else{
        //그게 아니라면 회원 전용으로 권한 인증
        auth = tokenProvider.getAuthenticationFromMember(token);
      }
      SecurityContextHolder.getContext().setAuthentication(auth);
    }

    filterChain.doFilter(request, response);
  }

  private String resolveTokenFromRequest(HttpServletRequest request){
    String token = request.getHeader(TOKEN_HEADER);

    if (!ObjectUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)){
      return token.substring(TOKEN_PREFIX.length());
    }

    return null;
  }
}
