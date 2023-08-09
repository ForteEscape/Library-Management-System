package com.management.library.controller.auth;

import com.management.library.controller.member.dto.MemberSignInDto;
import com.management.library.security.TokenProvider;
import com.management.library.service.member.MemberService;
import com.management.library.service.member.dto.MemberSignInResultDto;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberAuthController {

  private final MemberService memberService;
  private final TokenProvider tokenProvider;

  @PostMapping("/sign-in")
  public ResponseEntity<?> signIn(@Valid @RequestBody MemberSignInDto signIn){
    MemberSignInResultDto authenticate = memberService.authenticate(signIn.getMemberCode(),
        signIn.getPassword());

    String token = tokenProvider.generateToken(authenticate.getMemberCode(),
        authenticate.getAuthority());

    return new ResponseEntity<>(token, HttpStatus.OK);
  }
}
