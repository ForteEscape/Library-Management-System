package com.management.library.repository;

import com.management.library.domain.Hello;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Hello, Long> {

}
