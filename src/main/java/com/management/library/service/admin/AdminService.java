package com.management.library.service.admin;

import static com.management.library.exception.ErrorCode.ADMIN_ALREADY_EXISTS;
import static com.management.library.exception.ErrorCode.ADMIN_NOT_EXISTS;
import static com.management.library.exception.ErrorCode.PASSWORD_NOT_MATCH;
import static com.management.library.service.admin.dto.AdminServiceCreateDto.Request;
import static com.management.library.service.admin.dto.AdminServiceCreateDto.Response;

import com.management.library.domain.admin.Administrator;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.LoginFailedException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.service.admin.dto.AdminSignInResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminService implements UserDetailsService {

  private final AdministratorRepository administratorRepository;
  private final PasswordEncoder passwordEncoder;

  // ADMIN 관련 서비스
  // admin 등록 및 삭제

  @Transactional
  public Response createAdmin(Request request){
    if (administratorRepository.existsByEmail(request.getEmail())){
      throw new DuplicateException(ADMIN_ALREADY_EXISTS);
    }

    request.setPassword(passwordEncoder.encode(request.getPassword()));
    Administrator savedAdmin = administratorRepository.save(Administrator.of(request));
    return Response.of(savedAdmin);
  }

  @Transactional
  public void deleteAdmin(String email){
    if (!administratorRepository.existsByEmail(email)){
      throw new NoSuchElementExistsException(ADMIN_NOT_EXISTS);
    }

    administratorRepository.deleteByEmail(email);
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return administratorRepository.findByEmail(username)
        .orElseThrow(() -> new NoSuchElementExistsException(ADMIN_NOT_EXISTS));
  }

  public AdminSignInResultDto authenticate(String adminEmail, String password) {
    Administrator administrator = administratorRepository.findByEmail(adminEmail)
        .orElseThrow(() -> new NoSuchElementExistsException(ADMIN_NOT_EXISTS));

    if (!passwordEncoder.matches(password, administrator.getPassword())){
      throw new LoginFailedException(PASSWORD_NOT_MATCH);
    }

    return new AdminSignInResultDto(adminEmail, administrator.getAuthority());
  }
}
