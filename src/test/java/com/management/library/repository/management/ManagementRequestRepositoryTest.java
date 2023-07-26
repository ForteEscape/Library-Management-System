package com.management.library.repository.management;

import static com.management.library.domain.type.MemberRentalStatus.RENTAL_AVAILABLE;
import static com.management.library.domain.type.RequestStatus.*;
import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.management.ManagementRequest;
import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.type.Authority;
import com.management.library.domain.type.MemberRentalStatus;
import com.management.library.domain.type.RequestStatus;
import com.management.library.dto.RequestSearchCond;
import com.management.library.repository.member.MemberRepository;
import com.management.library.service.request.management.dto.ManagementRequestServiceDto.Response;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class ManagementRequestRepositoryTest {

  @Autowired
  private ManagementRequestRepository managementRequestRepository;

  @Autowired
  private MemberRepository memberRepository;

  @DisplayName("특정 회원의 모든 운영 개선 요청을 조회한다. 한 페이지에 최대 5개의 결과가 들어간다.")
  @Test
  public void findByMemberCode() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    ManagementRequest request1 = createManagementRequest(member1, "운영 약정 개정", AWAIT);
    ManagementRequest request2 = createManagementRequest(member1, "화장실 개선 요청", ACCEPTED);
    ManagementRequest request3 = createManagementRequest(member1, "운영시간 연장 요청", REFUSED);
    ManagementRequest request4 = createManagementRequest(member2, "주차장 규제 요청", AWAIT);
    ManagementRequest request5 = createManagementRequest(member2, "멀티미디어관 개선 요청", AWAIT);

    managementRequestRepository.saveAll(List.of(request1, request2, request3, request4, request5));

    PageRequest pageRequest = PageRequest.of(0, 5);

    // when
    Page<Response> requests = managementRequestRepository.findByMemberCode(
        member1.getMemberCode(), pageRequest);
    List<Response> content = requests.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("title", "content", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("운영 약정 개정", "운영 약정 개정 의 내용", AWAIT),
            tuple("화장실 개선 요청", "화장실 개선 요청 의 내용", ACCEPTED),
            tuple("운영시간 연장 요청", "운영시간 연장 요청 의 내용", REFUSED)
        );
  }

  @DisplayName("모든 운영 개선 요청 데이터를 조회한다.")
  @Test
  public void findAllWithNoCondition() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    ManagementRequest request1 = createManagementRequest(member1, "운영 약정 개정", AWAIT);
    ManagementRequest request2 = createManagementRequest(member1, "화장실 개선 요청", ACCEPTED);
    ManagementRequest request3 = createManagementRequest(member1, "운영시간 연장 요청", REFUSED);
    ManagementRequest request4 = createManagementRequest(member2, "주차장 규제 요청", AWAIT);
    ManagementRequest request5 = createManagementRequest(member2, "멀티미디어관 개선 요청", AWAIT);

    managementRequestRepository.saveAll(List.of(request1, request2, request3, request4, request5));

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();

    // when
    Page<ManagementRequest> requests = managementRequestRepository.findAll(cond, pageRequest);
    List<ManagementRequest> content = requests.getContent();

    // then
    assertThat(content).hasSize(5)
        .extracting("title", "content", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("운영 약정 개정", "운영 약정 개정 의 내용", AWAIT),
            tuple("화장실 개선 요청", "화장실 개선 요청 의 내용", ACCEPTED),
            tuple("운영시간 연장 요청", "운영시간 연장 요청 의 내용", REFUSED),
            tuple("주차장 규제 요청", "주차장 규제 요청 의 내용", AWAIT),
            tuple("멀티미디어관 개선 요청", "멀티미디어관 개선 요청 의 내용", AWAIT)
        );

    assertThat(content)
        .extracting(ManagementRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("park", "123457"),
            tuple("park", "123457")
        );
  }

  @DisplayName("결과 대기 중인 운영 개선 요청 데이터를 조회한다.")
  @Test
  public void findAllWithAwaiting() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    ManagementRequest request1 = createManagementRequest(member1, "운영 약정 개정", AWAIT);
    ManagementRequest request2 = createManagementRequest(member1, "화장실 개선 요청", ACCEPTED);
    ManagementRequest request3 = createManagementRequest(member1, "운영시간 연장 요청", REFUSED);
    ManagementRequest request4 = createManagementRequest(member2, "주차장 규제 요청", AWAIT);
    ManagementRequest request5 = createManagementRequest(member2, "멀티미디어관 개선 요청", AWAIT);

    managementRequestRepository.saveAll(List.of(request1, request2, request3, request4, request5));

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(AWAIT);

    // when
    Page<ManagementRequest> requests = managementRequestRepository.findAll(cond, pageRequest);
    List<ManagementRequest> content = requests.getContent();

    // then
    assertThat(content).hasSize(3)
        .extracting("title", "content", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("운영 약정 개정", "운영 약정 개정 의 내용", AWAIT),
            tuple("주차장 규제 요청", "주차장 규제 요청 의 내용", AWAIT),
            tuple("멀티미디어관 개선 요청", "멀티미디어관 개선 요청 의 내용", AWAIT)
        );

    assertThat(content)
        .extracting(ManagementRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("park", "123457"),
            tuple("park", "123457")
        );
  }

  @DisplayName("수락된 운영 개선 요청 데이터를 조회한다.")
  @Test
  public void findAllWithAccepted() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    ManagementRequest request1 = createManagementRequest(member1, "운영 약정 개정", AWAIT);
    ManagementRequest request2 = createManagementRequest(member1, "화장실 개선 요청", ACCEPTED);
    ManagementRequest request3 = createManagementRequest(member1, "운영시간 연장 요청", REFUSED);
    ManagementRequest request4 = createManagementRequest(member2, "주차장 규제 요청", ACCEPTED);
    ManagementRequest request5 = createManagementRequest(member2, "멀티미디어관 개선 요청", AWAIT);

    managementRequestRepository.saveAll(List.of(request1, request2, request3, request4, request5));

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(ACCEPTED);

    // when
    Page<ManagementRequest> requests = managementRequestRepository.findAll(cond, pageRequest);
    List<ManagementRequest> content = requests.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("title", "content", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("화장실 개선 요청", "화장실 개선 요청 의 내용", ACCEPTED),
            tuple("주차장 규제 요청", "주차장 규제 요청 의 내용", ACCEPTED)
        );

    assertThat(content)
        .extracting(ManagementRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("park", "123457")
        );
  }

  @DisplayName("거절된 운영 개선 요청 데이터를 조회한다.")
  @Test
  public void findAllWithRefused() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    ManagementRequest request1 = createManagementRequest(member1, "운영 약정 개정", AWAIT);
    ManagementRequest request2 = createManagementRequest(member1, "화장실 개선 요청", ACCEPTED);
    ManagementRequest request3 = createManagementRequest(member1, "운영시간 연장 요청", REFUSED);
    ManagementRequest request4 = createManagementRequest(member2, "주차장 규제 요청", REFUSED);
    ManagementRequest request5 = createManagementRequest(member2, "멀티미디어관 개선 요청", AWAIT);

    managementRequestRepository.saveAll(List.of(request1, request2, request3, request4, request5));

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(REFUSED);

    // when
    Page<ManagementRequest> requests = managementRequestRepository.findAll(cond, pageRequest);
    List<ManagementRequest> content = requests.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("title", "content", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("운영시간 연장 요청", "운영시간 연장 요청 의 내용", REFUSED),
            tuple("주차장 규제 요청", "주차장 규제 요청 의 내용", REFUSED)
        );

    assertThat(content)
        .extracting(ManagementRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("park", "123457")
        );
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

  private static Member createMember(String name, MemberRentalStatus memberRentalStatus,
      String memberCode) {
    Address address = Address.builder()
        .legion("경상남도")
        .city("김해시")
        .street("삼계로")
        .build();

    return Member.builder()
        .name(name)
        .birthdayCode("980101")
        .memberRentalStatus(memberRentalStatus)
        .memberCode(memberCode)
        .address(address)
        .password("1234")
        .authority(Authority.ROLE_MEMBER)
        .build();
  }
}