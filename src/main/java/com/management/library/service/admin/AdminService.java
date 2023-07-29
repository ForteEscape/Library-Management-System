package com.management.library.service.admin;

import static com.management.library.exception.ErrorCode.ADMIN_ALREADY_EXISTS;
import static com.management.library.exception.ErrorCode.ADMIN_NOT_EXISTS;
import static com.management.library.service.admin.dto.AdminCreateServiceDto.Request;
import static com.management.library.service.admin.dto.AdminCreateServiceDto.Response;

import com.management.library.domain.admin.Administrator;
import com.management.library.exception.DuplicateException;
import com.management.library.exception.NoSuchElementExistsException;
import com.management.library.repository.admin.AdministratorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AdminService {

  private final AdministratorRepository administratorRepository;

  // ADMIN 관련 서비스
  // admin 등록 및 삭제

  @Transactional
  public Response createAdmin(Request request){
    if (administratorRepository.existsByEmail(request.getEmail())){
      throw new DuplicateException(ADMIN_ALREADY_EXISTS);
    }

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

}
