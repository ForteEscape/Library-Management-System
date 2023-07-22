package com.management.library.repository.admin;

import com.management.library.domain.admin.Administrator;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministratorRepository extends JpaRepository<Administrator, Long> {

  Optional<Administrator> findByEmail(String email);
}
