package com.management.library.domain;

import static org.junit.jupiter.api.Assertions.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class HelloTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  void entityTest(){
    // given
    Hello hello = new Hello();
    hello.setName("member1");

    em.persist(hello);

    // when
    Hello findById = em.find(Hello.class, hello.getId());

    // then
    Assertions.assertThat(findById).isEqualTo(hello);
  }

}