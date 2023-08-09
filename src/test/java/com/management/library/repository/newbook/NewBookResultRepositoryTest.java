package com.management.library.repository.newbook;

import static com.management.library.domain.type.Authority.ROLE_ADMIN;
import static com.management.library.domain.type.Authority.ROLE_MEMBER;
import static com.management.library.domain.type.RequestStatus.ACCEPTED;
import static com.management.library.domain.type.RequestStatus.AWAIT;
import static com.management.library.domain.type.RequestStatus.REFUSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import com.management.library.domain.admin.Administrator;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.newbook.NewBookRequest;
import com.management.library.domain.newbook.NewBookRequestResult;
import com.management.library.domain.type.RequestStatus;
import com.management.library.repository.admin.AdministratorRepository;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.result.newbook.dto.NewBookResultCreateDto.Response;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
@Slf4j
class NewBookResultRepositoryTest {

  @Autowired
  private NewBookRequestResultRepository newBookRequestResultRepository;

  @Autowired
  private NewBookRequestRepository newBookRequestRepository;

  @Autowired
  private AdministratorRepository administratorRepository;

  @Autowired
  private MemberRepository memberRepository;

  @DisplayName("요청 아이디를 통해 요청 결과를 조회할 수 있다.")
  @Test
  public void findByRequestId() throws Exception {
    // given
    Member member = createMember("kim", "123456");
    memberRepository.save(member);

    NewBookRequest newBookRequest1 = createNewBookRequest(member, "jpa", AWAIT);
    NewBookRequest newBookRequest2 = createNewBookRequest(member, "jpa2", AWAIT);
    NewBookRequest newBookRequest3 = createNewBookRequest(member, "spring", AWAIT);
    newBookRequestRepository.saveAll(List.of(newBookRequest1, newBookRequest2, newBookRequest3));

    Administrator admin = createAdmin("admin", "admin@test.com", "1234");
    administratorRepository.save(admin);

    NewBookRequestResult result1 = createNewResult("도서 요청 결과", ACCEPTED, newBookRequest1, admin);
    NewBookRequestResult result2 = createNewResult("도서 요청 결과", ACCEPTED, newBookRequest2, admin);
    NewBookRequestResult result3 = createNewResult("도서 요청 결과", ACCEPTED, newBookRequest3, admin);
    newBookRequestResultRepository.saveAll(List.of(result1, result2, result3));

    // when
    NewBookRequestResult requestResult = newBookRequestResultRepository.findByRequestId(
            newBookRequest1.getId())
        .orElseThrow(() -> new IllegalArgumentException("해당 요청이 존재하지 않습니다."));

    // then
    assertThat(requestResult)
        .extracting("resultPostTitle", "resultPostContent", "result")
        .contains("도서 요청 결과", "도서 요청 결과 내용", ACCEPTED);

    assertThat(requestResult)
        .extracting(NewBookRequestResult::getAdministrator)
        .extracting(Administrator::getName, Administrator::getEmail)
        .contains("admin", "admin@test.com");

    assertThat(requestResult)
        .extracting(NewBookRequestResult::getNewBookRequest)
        .extracting(NewBookRequest::getRequestBookTitle, NewBookRequest::getRequestContent)
        .contains("jpa", "jpa 를 요청합니다.");
  }

  @DisplayName("요청 결과가 없다면 예외를 발생시킨다.")
  @Test
  public void findByRequestIdWithNoResult() throws Exception {

    // when
    // then
    assertThatThrownBy(() -> newBookRequestResultRepository.findByRequestId(1L)
        .orElseThrow(() -> new IllegalArgumentException("해당 요청이 존재하지 않습니다.")))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("해당 요청이 존재하지 않습니다.");
  }

  @DisplayName("관리자 이메일을 통해 요청 결과를 조회할 수 있다.")
  @Test
  public void findByAdminId() throws Exception {
    // given
    Member member1 = createMember("kim", "123456");
    Member member2 = createMember("park", "123457");
    memberRepository.saveAll(List.of(member1, member2));

    NewBookRequest newBookRequest1 = createNewBookRequest(member1, "jpa", AWAIT);
    NewBookRequest newBookRequest2 = createNewBookRequest(member1, "jpa2", AWAIT);
    NewBookRequest newBookRequest3 = createNewBookRequest(member1, "spring", AWAIT);
    NewBookRequest newBookRequest4 = createNewBookRequest(member1, "spring2", AWAIT);
    NewBookRequest newBookRequest5 = createNewBookRequest(member1, "docker", AWAIT);
    NewBookRequest newBookRequest6 = createNewBookRequest(member1, "docker2", AWAIT);
    NewBookRequest newBookRequest7 = createNewBookRequest(member1, "aws", AWAIT);

    newBookRequestRepository.saveAll(List.of(
        newBookRequest1, newBookRequest2, newBookRequest3, newBookRequest4,
        newBookRequest5, newBookRequest6, newBookRequest7
    ));

    Administrator admin = createAdmin("admin", "admin@test.com", "1234");
    administratorRepository.save(admin);

    NewBookRequestResult result1 = createNewResult("도서 요청 결과", ACCEPTED, newBookRequest1, admin);
    NewBookRequestResult result2 = createNewResult("도서 요청 결과", ACCEPTED, newBookRequest2, admin);
    NewBookRequestResult result3 = createNewResult("도서 요청 결과", ACCEPTED, newBookRequest3, admin);
    NewBookRequestResult result4 = createNewResult("도서 요청 결과", ACCEPTED, newBookRequest4, admin);
    NewBookRequestResult result5 = createNewResult("도서 요청 결과", ACCEPTED, newBookRequest5, admin);
    NewBookRequestResult result6 = createNewResult("도서 요청 결과", REFUSED, newBookRequest6, admin);
    NewBookRequestResult result7 = createNewResult("도서 요청 결과", REFUSED, newBookRequest7, admin);

    newBookRequestResultRepository.saveAll(List.of(
        result1, result2, result3, result4,
        result5, result6, result7
    ));

    PageRequest pageRequest1 = PageRequest.of(0, 5);
    PageRequest pageRequest2 = PageRequest.of(1, 5);

    // when
    Page<Response> requestResult1 = newBookRequestResultRepository.findByAdminId(
        admin.getEmail(), pageRequest1);
    List<Response> content1 = requestResult1.getContent();

    Page<Response> requestResult2 = newBookRequestResultRepository.findByAdminId(
        admin.getEmail(), pageRequest2);
    List<Response> content2 = requestResult2.getContent();

    // then
    assertThat(content1).hasSize(5)
        .extracting("adminName", "resultPostTitle", "resultPostContent", "resultStatus")
        .containsExactlyInAnyOrder(
            tuple("admin", "도서 요청 결과", "도서 요청 결과 내용", ACCEPTED),
            tuple("admin", "도서 요청 결과", "도서 요청 결과 내용", ACCEPTED),
            tuple("admin", "도서 요청 결과", "도서 요청 결과 내용", ACCEPTED),
            tuple("admin", "도서 요청 결과", "도서 요청 결과 내용", ACCEPTED),
            tuple("admin", "도서 요청 결과", "도서 요청 결과 내용", ACCEPTED)
        );

    assertThat(content2).hasSize(2)
        .extracting("adminName", "resultPostTitle", "resultPostContent", "resultStatus")
        .containsExactlyInAnyOrder(
            tuple("admin", "도서 요청 결과", "도서 요청 결과 내용", REFUSED),
            tuple("admin", "도서 요청 결과", "도서 요청 결과 내용", REFUSED)
        );
  }

  private static NewBookRequestResult createNewResult(String resultTitle,
      RequestStatus requestStatus, NewBookRequest request, Administrator administrator) {
    return NewBookRequestResult.builder()
        .resultPostTitle(resultTitle)
        .resultPostContent(resultTitle + " 내용")
        .result(requestStatus)
        .newBookRequest(request)
        .administrator(administrator)
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

  private static NewBookRequest createNewBookRequest(Member member, String requestBookTitle,
      RequestStatus requestStatus) {
    return NewBookRequest.builder()
        .member(member)
        .requestBookTitle(requestBookTitle)
        .requestContent(requestBookTitle + " 를 요청합니다.")
        .requestStatus(requestStatus)
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