package com.management.library.repository.newbook;

import static com.management.library.domain.type.MemberRentalStatus.RENTAL_AVAILABLE;
import static com.management.library.domain.type.RequestStatus.*;
import static org.assertj.core.api.Assertions.*;

import com.management.library.domain.member.Address;
import com.management.library.domain.member.Member;
import com.management.library.domain.newbook.NewBookRequest;
import com.management.library.domain.type.Authority;
import com.management.library.domain.type.MemberRentalStatus;
import com.management.library.domain.type.RequestStatus;
import com.management.library.dto.RequestSearchCond;
import com.management.library.repository.member.MemberRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@DataJpaTest
class NewBookRequestRepositoryTest {

  @Autowired
  private NewBookRequestRepository newBookRequestRepository;

  @Autowired
  private MemberRepository memberRepository;

  @DisplayName("모든 신간 요청 데이터를 조회한다. 한 페이지에 최대 5개의 결과가 들어간다.")
  @Test
  public void findAllByRequestSearchCond() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    NewBookRequest newBookRequest1 = createNewBookRequest(member1, "jpa", ACCEPTED);
    NewBookRequest newBookRequest2 = createNewBookRequest(member1, "jpa2", ACCEPTED);
    NewBookRequest newBookRequest3 = createNewBookRequest(member1, "spring", AWAIT);
    NewBookRequest newBookRequest4 = createNewBookRequest(member1, "spring2", AWAIT);
    NewBookRequest newBookRequest5 = createNewBookRequest(member1, "docker", AWAIT);
    NewBookRequest newBookRequest6 = createNewBookRequest(member1, "docker2", AWAIT);
    NewBookRequest newBookRequest7 = createNewBookRequest(member2, "aws", AWAIT);
    NewBookRequest newBookRequest8 = createNewBookRequest(member2, "aws2", AWAIT);

    newBookRequestRepository.saveAll(
        List.of(
            newBookRequest1, newBookRequest2, newBookRequest3, newBookRequest4,
            newBookRequest5, newBookRequest6, newBookRequest7, newBookRequest8
        )
    );

    PageRequest pageRequest1 = PageRequest.of(0, 5);
    PageRequest pageRequest2 = PageRequest.of(1, 5);
    RequestSearchCond cond = new RequestSearchCond();

    // when
    Page<NewBookRequest> newBookRequestPage1 = newBookRequestRepository.findAll(cond, pageRequest1);
    List<NewBookRequest> content1 = newBookRequestPage1.getContent();

    Page<NewBookRequest> newBookRequestPage2 = newBookRequestRepository.findAll(cond, pageRequest2);
    List<NewBookRequest> content2 = newBookRequestPage2.getContent();

    // then
    assertThat(content1).hasSize(5)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", "jpa 를 요청합니다.", ACCEPTED),
            tuple("jpa2", "jpa2 를 요청합니다.", ACCEPTED),
            tuple("spring", "spring 를 요청합니다.", AWAIT),
            tuple("spring2", "spring2 를 요청합니다.", AWAIT),
            tuple("docker", "docker 를 요청합니다.", AWAIT)
        );

    assertThat(content1)
        .extracting(NewBookRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("kim", "123456")
        );

    assertThat(content2).hasSize(3)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("docker2", "docker2 를 요청합니다.", AWAIT),
            tuple("aws", "aws 를 요청합니다.", AWAIT),
            tuple("aws2", "aws2 를 요청합니다.", AWAIT)
        );

    assertThat(content2)
        .extracting(NewBookRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("park", "123457"),
            tuple("park", "123457")
        );
  }

  @DisplayName("대기 중인 신간 요청 데이터를 조회한다. 한 페이지에 최대 5개의 결과가 들어간다.")
  @Test
  public void findAllByRequestSearchCondAwait() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    NewBookRequest newBookRequest1 = createNewBookRequest(member1, "jpa", ACCEPTED);
    NewBookRequest newBookRequest2 = createNewBookRequest(member1, "jpa2", ACCEPTED);
    NewBookRequest newBookRequest3 = createNewBookRequest(member1, "spring", AWAIT);
    NewBookRequest newBookRequest4 = createNewBookRequest(member1, "spring2", AWAIT);
    NewBookRequest newBookRequest5 = createNewBookRequest(member1, "docker", AWAIT);
    NewBookRequest newBookRequest6 = createNewBookRequest(member1, "docker2", AWAIT);
    NewBookRequest newBookRequest7 = createNewBookRequest(member2, "aws", AWAIT);
    NewBookRequest newBookRequest8 = createNewBookRequest(member2, "aws2", AWAIT);

    newBookRequestRepository.saveAll(
        List.of(
            newBookRequest1, newBookRequest2, newBookRequest3, newBookRequest4,
            newBookRequest5, newBookRequest6, newBookRequest7, newBookRequest8
        )
    );

    PageRequest pageRequest1 = PageRequest.of(0, 5);
    PageRequest pageRequest2 = PageRequest.of(1, 5);
    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(AWAIT);

    // when
    Page<NewBookRequest> newBookRequestPage1 = newBookRequestRepository.findAll(cond, pageRequest1);
    List<NewBookRequest> content1 = newBookRequestPage1.getContent();

    Page<NewBookRequest> newBookRequestPage2 = newBookRequestRepository.findAll(cond, pageRequest2);
    List<NewBookRequest> content2 = newBookRequestPage2.getContent();

    // then
    assertThat(content1).hasSize(5)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("spring", "spring 를 요청합니다.", AWAIT),
            tuple("spring2", "spring2 를 요청합니다.", AWAIT),
            tuple("docker", "docker 를 요청합니다.", AWAIT),
            tuple("docker2", "docker2 를 요청합니다.", AWAIT),
            tuple("aws", "aws 를 요청합니다.", AWAIT)
        );

    assertThat(content1)
        .extracting(NewBookRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("park", "123457")
        );

    assertThat(content2).hasSize(1)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("aws2", "aws2 를 요청합니다.", AWAIT)
        );

    assertThat(content2)
        .extracting(NewBookRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("park", "123457")
        );
  }

  @DisplayName("수락된 신간 요청 데이터를 조회한다. 한 페이지에 최대 5개의 결과가 들어간다.")
  @Test
  public void findAllByRequestSearchCondAccepted() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    NewBookRequest newBookRequest1 = createNewBookRequest(member1, "jpa", ACCEPTED);
    NewBookRequest newBookRequest2 = createNewBookRequest(member1, "jpa2", ACCEPTED);
    NewBookRequest newBookRequest3 = createNewBookRequest(member1, "spring", AWAIT);
    NewBookRequest newBookRequest4 = createNewBookRequest(member1, "spring2", AWAIT);
    NewBookRequest newBookRequest5 = createNewBookRequest(member1, "docker", AWAIT);
    NewBookRequest newBookRequest6 = createNewBookRequest(member1, "docker2", AWAIT);
    NewBookRequest newBookRequest7 = createNewBookRequest(member2, "aws", AWAIT);
    NewBookRequest newBookRequest8 = createNewBookRequest(member2, "aws2", AWAIT);

    newBookRequestRepository.saveAll(
        List.of(
            newBookRequest1, newBookRequest2, newBookRequest3, newBookRequest4,
            newBookRequest5, newBookRequest6, newBookRequest7, newBookRequest8
        )
    );

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(ACCEPTED);

    // when
    Page<NewBookRequest> newBookRequestPage1 = newBookRequestRepository.findAll(cond, pageRequest);
    List<NewBookRequest> content = newBookRequestPage1.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", "jpa 를 요청합니다.", ACCEPTED),
            tuple("jpa2", "jpa2 를 요청합니다.", ACCEPTED)
        );

    assertThat(content)
        .extracting(NewBookRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("kim", "123456")
        );
  }

  @DisplayName("수락된 신간 요청 데이터를 조회한다. 한 페이지에 최대 5개의 결과가 들어간다.")
  @Test
  public void findAllByRequestSearchCondRefused() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    NewBookRequest newBookRequest1 = createNewBookRequest(member1, "jpa", REFUSED);
    NewBookRequest newBookRequest2 = createNewBookRequest(member1, "jpa2", REFUSED);
    NewBookRequest newBookRequest3 = createNewBookRequest(member1, "spring", AWAIT);
    NewBookRequest newBookRequest4 = createNewBookRequest(member1, "spring2", AWAIT);
    NewBookRequest newBookRequest5 = createNewBookRequest(member1, "docker", AWAIT);
    NewBookRequest newBookRequest6 = createNewBookRequest(member1, "docker2", AWAIT);
    NewBookRequest newBookRequest7 = createNewBookRequest(member2, "aws", AWAIT);
    NewBookRequest newBookRequest8 = createNewBookRequest(member2, "aws2", AWAIT);

    newBookRequestRepository.saveAll(
        List.of(
            newBookRequest1, newBookRequest2, newBookRequest3, newBookRequest4,
            newBookRequest5, newBookRequest6, newBookRequest7, newBookRequest8
        )
    );

    PageRequest pageRequest = PageRequest.of(0, 5);
    RequestSearchCond cond = new RequestSearchCond();
    cond.setRequestStatus(REFUSED);

    // when
    Page<NewBookRequest> newBookRequestPage1 = newBookRequestRepository.findAll(cond, pageRequest);
    List<NewBookRequest> content = newBookRequestPage1.getContent();

    // then
    assertThat(content).hasSize(2)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", "jpa 를 요청합니다.", REFUSED),
            tuple("jpa2", "jpa2 를 요청합니다.", REFUSED)
        );

    assertThat(content)
        .extracting(NewBookRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("kim", "123456")
        );
  }

  @DisplayName("특정 회원의 모든 신간 요청 데이터를 조회한다. 한 페이지에 최대 5개의 결과가 들어간다.")
  @Test
  public void findByMemberCode() throws Exception {
    // given
    Member member1 = createMember("kim", RENTAL_AVAILABLE, "123456");
    Member member2 = createMember("park", RENTAL_AVAILABLE, "123457");

    memberRepository.saveAll(List.of(member1, member2));

    NewBookRequest newBookRequest1 = createNewBookRequest(member1, "jpa", ACCEPTED);
    NewBookRequest newBookRequest2 = createNewBookRequest(member1, "jpa2", ACCEPTED);
    NewBookRequest newBookRequest3 = createNewBookRequest(member1, "spring", AWAIT);
    NewBookRequest newBookRequest4 = createNewBookRequest(member1, "spring2", AWAIT);
    NewBookRequest newBookRequest5 = createNewBookRequest(member1, "docker", AWAIT);
    NewBookRequest newBookRequest6 = createNewBookRequest(member1, "docker2", AWAIT);
    NewBookRequest newBookRequest7 = createNewBookRequest(member2, "aws", AWAIT);
    NewBookRequest newBookRequest8 = createNewBookRequest(member2, "aws2", AWAIT);

    newBookRequestRepository.saveAll(
        List.of(
            newBookRequest1, newBookRequest2, newBookRequest3, newBookRequest4,
            newBookRequest5, newBookRequest6, newBookRequest7, newBookRequest8
        )
    );

    PageRequest pageRequest1 = PageRequest.of(0, 5);
    PageRequest pageRequest2 = PageRequest.of(1, 5);

    // when
    Page<NewBookRequest> newBookRequestPage1 = newBookRequestRepository.findByMemberCode(
        member1.getMemberCode(), pageRequest1);
    List<NewBookRequest> content1 = newBookRequestPage1.getContent();

    Page<NewBookRequest> newBookRequestPage2 = newBookRequestRepository.findByMemberCode(
        member1.getMemberCode(), pageRequest2);
    List<NewBookRequest> content2 = newBookRequestPage2.getContent();

    // then
    assertThat(content1).hasSize(5)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("jpa", "jpa 를 요청합니다.", ACCEPTED),
            tuple("jpa2", "jpa2 를 요청합니다.", ACCEPTED),
            tuple("spring", "spring 를 요청합니다.", AWAIT),
            tuple("spring2", "spring2 를 요청합니다.", AWAIT),
            tuple("docker", "docker 를 요청합니다.", AWAIT)
        );

    assertThat(content1)
        .extracting(NewBookRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("kim", "123456"),
            tuple("kim", "123456")
        );

    assertThat(content2).hasSize(1)
        .extracting("requestBookTitle", "requestContent", "requestStatus")
        .containsExactlyInAnyOrder(
            tuple("docker2", "docker2 를 요청합니다.", AWAIT)
        );

    assertThat(content2)
        .extracting(NewBookRequest::getMember)
        .extracting(Member::getName, Member::getMemberCode)
        .containsExactlyInAnyOrder(
            tuple("kim", "123456")
        );
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