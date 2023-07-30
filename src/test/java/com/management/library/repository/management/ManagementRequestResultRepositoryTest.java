package com.management.library.repository.management;

import static com.management.library.domain.type.Authority.ROLE_ADMIN;
import static com.management.library.domain.type.Authority.ROLE_MEMBER;
import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.AWAIT;
import static com.management.library.domain.type.RequestStatus.REFUSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.domain.admin.Administrator;
import com.management.library.domain.management.ManagementRequest;
import com.management.library.domain.management.ManagementRequestResult;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.type.RequestStatus;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.result.management.dto.ManagementResultCreateDto.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ManagementRequestResultRepositoryTest {

  @Autowired
  private ManagementRequestRepository managementRequestRepository;

  @Autowired
  private ManagementRequestResultRepository managementRequestResultRepository;

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private AdministratorRepository administratorRepository;

  @DisplayName("요청의 id를 통해 결과를 조회할 수 있다.")
  @Test
  public void findByRequestId() throws Exception {
    // given
    Member member = createMember("kim", "123456");
    memberRepository.save(member);

    Administrator administrator = createAdmin("admin1", "admin1@test.com", "1234");
    administratorRepository.save(administrator);

    ManagementRequest request1 = createManagementRequest(member, "운영 요청 1", AWAIT);
    ManagementRequest request2 = createManagementRequest(member, "운영 요청 2", AWAIT);
    ManagementRequest request3 = createManagementRequest(member, "운영 요청 3", AWAIT);

    managementRequestRepository.saveAll(List.of(request1, request2, request3));

    ManagementRequestResult result1 = createResult(request1, ACCEPTED, "운영 요청 1", administrator);
    ManagementRequestResult result2 = createResult(request2, ACCEPTED, "운영 요청 2", administrator);
    ManagementRequestResult result3 = createResult(request3, REFUSED, "운영 요청 3", administrator);

    managementRequestResultRepository.saveAll(List.of(result1, result2, result3));

    // when
    ManagementRequestResult result = managementRequestResultRepository.findByRequestId(
            request1.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 요청이 존재하지 않습니다."));

    // then
    assertThat(result)
        .extracting("resultPostTitle", "resultPostContent", "result")
        .contains("운영 요청 1", "운영 요청 1 의 결과입니다.", ACCEPTED);

    assertThat(result)
        .extracting(ManagementRequestResult::getManagementRequest)
        .extracting(ManagementRequest::getId)
        .isEqualTo(request1.getId());
  }

  @DisplayName("아직 답변이 없거나 존재하지 않는 요청일 경우 예외를 발생시킨다.")
  @Test
  public void findByRequestIdRequestNotExists() throws Exception {
    // when
    // then
    assertThatThrownBy(() -> managementRequestResultRepository.findByRequestId(1L)
        .orElseThrow(() -> new IllegalArgumentException("해당 요청이 존재하지 않습니다.")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 요청이 존재하지 않습니다.");
  }

  @DisplayName("특정 관리자가 답변한 데이터를 조회할 수 있다.")
  @Test
  public void findByAdminEmail() throws Exception {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("park", "123457");
    memberRepository.saveAll(List.of(member1, member2));

    Administrator admin1 = createAdmin("admin1", "admin1@test.com", "1234");
    Administrator admin2 = createAdmin("admin2", "admin2@test.com", "5678");
    administratorRepository.saveAll(List.of(admin1, admin2));

    ManagementRequest request1 = createManagementRequest(member1, "운영 요청 1", AWAIT);
    ManagementRequest request2 = createManagementRequest(member1, "운영 요청 2", AWAIT);
    ManagementRequest request3 = createManagementRequest(member1, "운영 요청 3", AWAIT);
    ManagementRequest request4 = createManagementRequest(member2, "운영 요청 4", AWAIT);
    ManagementRequest request5 = createManagementRequest(member2, "운영 요청 5", AWAIT);

    managementRequestRepository.saveAll(List.of(request1, request2, request3, request4, request5));

    ManagementRequestResult result1 = createResult(request1, ACCEPTED, "운영 요청 1", admin1);
    ManagementRequestResult result2 = createResult(request2, ACCEPTED, "운영 요청 2", admin1);
    ManagementRequestResult result3 = createResult(request3, REFUSED, "운영 요청 3", admin1);
    ManagementRequestResult result4 = createResult(request3, ACCEPTED, "운영 요청 4", admin2);
    ManagementRequestResult result5 = createResult(request3, REFUSED, "운영 요청 5", admin2);

    managementRequestResultRepository.saveAll(List.of(result1, result2, result3, result4, result5));

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> resultList = managementRequestResultRepository.findByAdminEmail(
        admin1.getEmail(), pageRequest);
    List<Response> content = resultList.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("adminName", "resultPostTitle", "resultPostContent", "resultStatus")
        .containsExactlyInAnyOrder(
            tuple("admin1", "운영 요청 1", "운영 요청 1 의 결과입니다.", ACCEPTED),
            tuple("admin1", "운영 요청 2", "운영 요청 2 의 결과입니다.", ACCEPTED),
            tuple("admin1", "운영 요청 3", "운영 요청 3 의 결과입니다.", REFUSED)
        );
  }

  private static ManagementRequestResult createResult(ManagementRequest request,
      RequestStatus requestStatus, String resultPostTitle, Administrator administrator) {
    return ManagementRequestResult.builder()
        .managementRequest(request)
        .result(requestStatus)
        .resultPostTitle(resultPostTitle)
        .resultPostContent(resultPostTitle + " 의 결과입니다.")
        .administrator(administrator)
        .build();
  }

  private static ManagementRequest createManagementRequest(Member member, String title,
      RequestStatus requestStatus) {
    return ManagementRequest.builder()
        .member(member)
        .title(title)
        .content(title + " 의 내용")
        .requestStatus(requestStatus)
        .build();
  }

  private static Administrator createAdmin(String name, String email, String password) {
    return Administrator.builder()
        .name(name)
        .email(email)
        .password(password)
        .authority(ROLE_ADMIN)
        .build();
  }

  private static Member createMember(String name, String memberCode) {
    Address address = Address.builder()
        .legion("경상남도")
        .city("김해시")
        .street("삼계로")
        .build();

    return Member.builder()
        .name(name)
        .birthdayCode("980101")
        .memberCode(memberCode)
        .address(address)
        .password("1234")
        .authority(ROLE_MEMBER)
        .build();
  }
}