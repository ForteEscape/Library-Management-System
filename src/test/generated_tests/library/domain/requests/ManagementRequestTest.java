package library.domain.requests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.management.library.domain.requests.ManagementRequest;
import com.management.library.domain.type.RequestStatus;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class ManagementRequestTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  void objectTest(){
    ManagementRequest result = ManagementRequest.builder()
        .title("hello")
        .content("hello there")
        .requestStatus(RequestStatus.STAND_BY)
        .build();

    em.persist(result);

    em.flush();
    em.clear();

    ManagementRequest managementRequest = em.find(ManagementRequest.class, result.getId());

    assertThat(managementRequest.getTitle()).isEqualTo(result.getTitle());
    assertThat(managementRequest.getContent()).isEqualTo(result.getContent());
    assertThat(managementRequest.getId()).isEqualTo(result.getId());
  }

}